package com.khwu.lsh;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.khwu.model.cassandra.SimilarPeople;
import com.khwu.model.sql.Schema;
import com.khwu.util.Utility;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaSparkContext$;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.khwu.util.Utility.*;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.explode;

public class LSHJsonMain {
    private static final double THRESHOLD = 0.6;
    private static final int HASH_TABLES = 5;

    public static void main(String[] args) {
        Utility.setUpLogging();
        Properties prop;
        String master;

        SparkConf conf = new SparkConf()
                .setAppName("meetup-lsh");

        //noinspection Duplicates
        if (args.length > 0) {
            prop = Utility.setUpConfig(args[0]);
            conf.set("spark.driver.memory", "3g");
            conf.set("spark.executor.memory", "3g");
            conf.setMaster(args[1]);
        } else {
            prop = Utility.setUpConfig(Utility.DEBUG_MODE);
            conf.setMaster("local[*]");
        }

        conf.set("spark.cassandra.connection.host", prop.getProperty(Utility.CASSANDRA_HOST));
        conf.set("spark.cassandra.connection.port", prop.getProperty(Utility.CASSANDRA_PORT));

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        Map<String, String> code = jsc.textFile(prop.getProperty(COUNTRY_CODE))
                .filter(line -> !line.contains("English short name"))
                .mapToPair(line -> new Tuple2<>(line.split(",")[1], line.split(",")[2]))
                .collectAsMap();
        System.out.println("Country code: " + " " +code);
        Broadcast<Map<String, String>> bc = jsc.broadcast(code);

        StructType schema = Schema.schema();

        String[] files = prop.getProperty(Utility.DATA_SOURCE).split(",");

        Dataset<Row> df = spark.read()
                .schema(schema)
                .json(files);

        Dataset<Row> subDF = df.select("member.member_id",
                "member.member_name",
                "group.group_topics.urlkey",
                "group.group_country",
                "group.group_state");

        long urlKeyNum = subDF.select(explode(col("urlkey")))
                .distinct()
                .count() + 1;

        if (urlKeyNum >= Integer.MAX_VALUE) {
            urlKeyNum = Integer.MAX_VALUE;
        }

        System.out.println(String.format("Url-keys: %d", urlKeyNum));

        long memberNum = subDF.select("member_id")
                .distinct()
                .count();

        System.out.println(String.format("Members: %d", memberNum));

        CountVectorizerModel cvModel = new CountVectorizer()
                .setInputCol("urlkey")
                .setOutputCol("feature")
                .setVocabSize((int) urlKeyNum)
                .setMinDF(2)
                .fit(subDF);

        spark.udf().register("isNoneZeroVector", (Vector v) -> v.numNonzeros() > 0, DataTypes.BooleanType);

        Dataset<Row> vectorizedDF = cvModel.transform(subDF)
                .filter(callUDF("isNoneZeroVector", col("feature")))
                .select(col("member_id"),
                        col("member_name"),
                        col("urlkey"),
                        col("group_country"),
                        col("group_state"),
                        col("feature"));

        MinHashLSH mh = new MinHashLSH()
                .setNumHashTables(HASH_TABLES)
                .setInputCol("feature")
                .setOutputCol("hashValues");

        MinHashLSHModel model = mh.fit(vectorizedDF);

        model.transform(vectorizedDF)
                .show(false);

        Dataset<Row> key = vectorizedDF.where("member_id = 69177262");
        model.approxSimilarityJoin(vectorizedDF, key, THRESHOLD, "distance")
                .select(col("datasetA.member_id").alias("ida"),
                        col("datasetA.member_name").alias("name_a"),
                        col("datasetA.urlkey").alias("urlkey_a"),
                        col("datasetB.member_id").alias("idb"),
                        col("datasetB.member_name").alias("name_b"),
                        col("datasetB.urlkey").alias("urlkey_b"),
                        col("datasetB.group_country").alias("group_country"),
                        col("datasetB.group_state").alias("group_state"),
                        col("distance"))
                .where("ida != idb")
                .show(false);

        Dataset<Row> similarPPL = model
                .approxSimilarityJoin(vectorizedDF, vectorizedDF, THRESHOLD, "distance")
                .select(col("datasetA.member_id").alias("ida"),
                        col("datasetA.member_name").alias("name_a"),
                        col("datasetA.urlkey").alias("urlkey_a"),
                        col("datasetB.member_id").alias("idb"),
                        col("datasetB.member_name").alias("name_b"),
                        col("datasetB.urlkey").alias("urlkey_b"),
                        col("datasetB.group_country").alias("country_b"),
                        col("datasetB.group_state").alias("state_b"),
                        col("distance"))
                .where("ida != idb");

        similarPPL.show();

        spark.stop();
    }
}
