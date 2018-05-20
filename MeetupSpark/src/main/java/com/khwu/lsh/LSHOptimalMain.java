package com.khwu.lsh;

import com.khwu.lsh.algorithm.JaccardMinHashNNS;
import com.khwu.lsh.model.JaccardMinHashModel;
import com.khwu.model.sql.Schema;
import com.khwu.util.Utility;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static com.khwu.lsh.MeetupLSHMain.COUNTRY_CODE_HEADER;
import static com.khwu.lsh.MeetupLSHMain.CSV_SPLITTER;
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

        StructType schema = Schema.schema();

        String[] files = prop.getProperty(Utility.DATA_SOURCE).split(",");

        Dataset<Row> df = spark.read()
                .schema(schema)
                .json(files);

        Map<String, Integer> keys = df.select(explode(col("group.group_topics.urlkey")))
                .distinct()
                .toJavaRDD()
                .zipWithIndex()
                .mapToPair(tu -> new Tuple2<>(tu._1.getString(0), toIntExact(tu._2)))
                .collectAsMap();

//        System.out.println(keys);

        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        Map<String, String> code = jsc.textFile(prop.getProperty(COUNTRY_CODE))
                .filter(line -> !line.contains(COUNTRY_CODE_HEADER))
                .mapToPair(line -> new Tuple2<>(line.split(CSV_SPLITTER)[1], line.split(CSV_SPLITTER)[2]))
                .collectAsMap();

        System.out.println("Country code: " + " " +code);

        Broadcast<Map<String, String>> bcCode = jsc.broadcast(code);

        Broadcast<Map<String, Integer>> bcKey = jsc.broadcast(keys);

        JaccardMinHashModel model = new JaccardMinHashNNS()
                .setNumHashes(2)
                .setSignatureLength(1)
                .setNumOutputPartitions(1)
                .setBucketLimit(10)
                .setJoinParallelism(10)
                .setShouldSampleBuckets(true)
                .createModel(bcKey.value().size());

        JavaPairRDD<Long, Vector> rdd = df.select("member.member_id","group.group_topics.urlkey")
                .toJavaRDD()
                .map(row -> {
                    int[] idx = row.getList(1).stream().map(key -> bcKey.value().get(key)).mapToInt(i -> i).toArray();
                    Arrays.sort(idx);
                    double[] val = new double[idx.length];
                    Arrays.fill(val, 1.0);
                    Tuple2<Long, Vector> res = new Tuple2<>(row.getLong(0), Vectors.sparse(bcKey.value().size(), idx, val));
                    return res;
                }).mapToPair(x -> x);
//        rdd.foreach(tu -> {
//            System.out.println(tu._1 + " " +  tu._2);
//        });
        model.getAllNearestNeighbors(rdd, 10).foreach(tu -> {
            System.out.println(tu._1() + " " + tu._2() + " " + tu._3());
        });
    }
}
