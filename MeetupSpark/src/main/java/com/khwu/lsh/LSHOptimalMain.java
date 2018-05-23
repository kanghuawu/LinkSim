package com.khwu.lsh;

import com.khwu.lsh.algorithm.JaccardMinHashNNS;
import com.khwu.lsh.model.JaccardMinHashModel;
import com.khwu.model.cassandra.TagByUserId;
import com.khwu.model.sql.Schema;
import com.khwu.util.Utility;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;
import static com.khwu.lsh.MLLibLSHMain.COUNTRY_CODE_HEADER;
import static com.khwu.lsh.MLLibLSHMain.CSV_SPLITTER;
import static com.khwu.model.cassandra.SimilarPeople.saveToCassandra;
import static com.khwu.model.cassandra.TagByUserId.TAG_BY_USERID;
import static com.khwu.util.Utility.CASSANDRA_KEYSPACE;
import static com.khwu.util.Utility.COUNTRY_CODE;
import static java.lang.Math.toIntExact;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.explode;

public class LSHOptimalMain {
    public static void main(String[] args) {
        Utility.setUpLogging();
        Properties prop;
        String master;

        //noinspection Duplicates
        if (args.length > 0) {
            prop = Utility.setUpConfig(args[0]);
            master = args[1];
        } else {
            prop = Utility.setUpConfig(Utility.DEBUG_MODE);
            master = "local[*]";
        }

        if (prop == null) {
            System.out.println("Props missing...");
            return;
        }

        SparkConf conf = new SparkConf()
                .setMaster(master)
                .setAppName("meetup-lsh")
                .set("spark.cassandra.connection.host", prop.getProperty(Utility.CASSANDRA_HOST))
                .set("spark.cassandra.connection.port", prop.getProperty(Utility.CASSANDRA_PORT));

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        Map<String, String> code = jsc.textFile(prop.getProperty(COUNTRY_CODE))
                .filter(line -> !line.contains(COUNTRY_CODE_HEADER))
                .mapToPair(line -> new Tuple2<>(line.split(CSV_SPLITTER)[1], line.split(CSV_SPLITTER)[2]))
                .collectAsMap();

        System.out.println("Country code: " + " " +code);

        Broadcast<Map<String, String>> bc = jsc.broadcast(code);

        JavaRDD<TagByUserId> tagRdd = javaFunctions(jsc)
                .cassandraTable(CASSANDRA_KEYSPACE, TAG_BY_USERID, mapRowTo(TagByUserId.class));

        Dataset<Row> df = spark.createDataFrame(tagRdd, TagByUserId.class)
                .withColumnRenamed("id", "member_id")
                .withColumnRenamed("name", "member_name")
                .withColumnRenamed("country","group_country")
                .withColumnRenamed("state", "group_state")
                .withColumnRenamed("tag", "urlkey").cache();


        Map<String, Integer> keys = df.select("urlkey")
                .distinct()
                .toJavaRDD()
                .zipWithIndex()
                .mapToPair(tu -> new Tuple2<>(tu._1.getString(0), toIntExact(tu._2)))
                .collectAsMap();

        System.out.println("Country code: " + " " +code);

        Broadcast<Map<String, String>> bcCode = jsc.broadcast(code);

        Broadcast<Map<String, Integer>> bcKey = jsc.broadcast(keys);

        JaccardMinHashModel model = new JaccardMinHashNNS()
                .setNumHashes(120)
                .setSignatureLength(40)
                .setNumOutputPartitions(100)
                .setBucketLimit(1000)
                .setJoinParallelism(1000)
                .setShouldSampleBuckets(true)
                .createModel(bcKey.value().size());

        JavaPairRDD<Long, Vector> src = df.select("member_id","urlkey")
                .toJavaRDD()
                .map(row -> {
                    int[] idx = row.getList(1).stream().map(key -> bcKey.value().get(key)).mapToInt(i -> i).toArray();
                    Arrays.sort(idx);
                    double[] val = new double[idx.length];
                    Arrays.fill(val, 1.0);
                    Tuple2<Long, Vector> res = new Tuple2<>(row.getLong(0), Vectors.sparse(bcKey.value().size(), idx, val));
                    return res;
                }).mapToPair(x -> x);

        JavaRDD<Row> rdd = model.getAllNearestNeighbors(src, 1000)
                .map(tu -> RowFactory.create(tu._1(), tu._2(), tu._3()));

        StructType nbSchema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("ida", DataTypes.LongType, true),
                DataTypes.createStructField("idb", DataTypes.LongType, true),
                DataTypes.createStructField("distance", DataTypes.DoubleType, true),
        });

        Dataset<Row> nbDF = spark.createDataFrame(rdd, nbSchema);

        Dataset<Row> populatedDF = nbDF.join(df.select(col("member_id").alias("idA"),
                col("member_name").alias("nameA"),
                col("urlkey").alias("urlkeyA")))
                .join(df.select(col("member_id").alias("idB"),
                        col("member_name").alias("nameB"),
                        col("country").alias("countryB"),
                        col("state").alias("stateB"),
                        col("urlkey").alias("urlkeyB")))
                .select("idA", "nameA", "urlkeyA", "idB", "nameB", "urlkeyB", "countryB", "stateB", "distance");

        saveToCassandra(populatedDF, bcCode);
    }
}
