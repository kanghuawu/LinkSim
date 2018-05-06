package com.khwu.lsh;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.khwu.model.cassandra.SimilarPeople;
import com.khwu.model.sql.Schema;
import com.khwu.util.Utility;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.CountVectorizer;
import org.apache.spark.ml.feature.CountVectorizerModel;
import org.apache.spark.ml.feature.MinHashLSH;
import org.apache.spark.ml.feature.MinHashLSHModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.khwu.util.Utility.*;
import static org.apache.spark.sql.functions.callUDF;
import static org.apache.spark.sql.functions.col;

public class MeetupLSHMain {
    private static final double THRESHOLD = 0.3;
    private static final String SIMILAR_PEOPLE_TABLE = "similar_people";
    private static final int HASH_TABLES = 3;

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

        Dataset<Row> subDF = df.select("member.member_id", "member.member_name", "group.group_topics.urlkey");

        long urlKeyNum = subDF.select("urlkey")
                .distinct()
                .count();

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
                .select(col("member_id"),  col("member_name"), col("urlkey"), col("feature"));

        MinHashLSH mh = new MinHashLSH()
                .setNumHashTables(HASH_TABLES)
                .setInputCol("feature")
                .setOutputCol("hashValues");

        MinHashLSHModel model = mh.fit(vectorizedDF);

        model.transform(vectorizedDF)
                .show();

        Dataset<Row> similarPPL = model
                .approxSimilarityJoin(vectorizedDF, vectorizedDF, THRESHOLD, "distance")
                .select(col("datasetA.member_id").alias("ida"),
                        col("datasetA.member_name").alias("name_a"),
                        col("datasetA.urlkey").alias("urlkey_a"),
                        col("datasetB.member_id").alias("idb"),
                        col("datasetB.member_name").alias("name_b"),
                        col("datasetB.urlkey").alias("urlkey_b"),
                        col("distance"));

        similarPPL.show();

        JavaRDD<SimilarPeople> rdd = similarPPL
                .javaRDD()
                .filter(row -> row.getLong(0) != row.getLong(3))
                .map(row -> {
                    SimilarPeople s = new SimilarPeople();
                    s.setIdA(row.getLong(0));
                    s.setNameA(row.getString(1));
                    s.setUrlkeyA(row.getList(2));
                    s.setIdB(row.getLong(3));
                    s.setNameB(row.getString(4));
                    s.setUrlkeyB(row.getList(5));
                    s.setDistance(row.getDouble(6));
                    return s;
                });

        Map<String, String> fieldToColumnMapping = new HashMap<>();
        fieldToColumnMapping.put("idA", "id_a");
        fieldToColumnMapping.put("nameA", "name_a");
        fieldToColumnMapping.put("urlkeyA", "urlkey_a");
        fieldToColumnMapping.put("idB", "id_b");
        fieldToColumnMapping.put("nameB", "name_b");
        fieldToColumnMapping.put("urlkeyB", "urlkey_b");
        fieldToColumnMapping.put("distance", "distance");

        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, SIMILAR_PEOPLE_TABLE, CassandraJavaUtil.mapToRow(SimilarPeople.class, fieldToColumnMapping))
//                .withConstantTTL(30)
                .saveToCassandra();

        spark.stop();
    }
}
