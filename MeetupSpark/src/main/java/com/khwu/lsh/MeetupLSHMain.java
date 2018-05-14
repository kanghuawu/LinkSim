package com.khwu.lsh;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.khwu.model.cassandra.SimilarPeople;
import com.khwu.model.cassandra.TagByUserId;
import com.khwu.util.Utility;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.feature.CountVectorizer;
import org.apache.spark.ml.feature.CountVectorizerModel;
import org.apache.spark.ml.feature.MinHashLSH;
import org.apache.spark.ml.feature.MinHashLSHModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;
import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapRowTo;
import static com.khwu.util.Utility.*;
import static org.apache.spark.sql.functions.*;

public class MeetupLSHMain {
    private static final double THRESHOLD = 0.9;
    private static final String SIMILAR_PEOPLE_TABLE = "similar_people";
    private static final int HASH_TABLES = 5;
    private static final String COUNTRY_CODE_HEADER = "English short name";
    private static final String CSV_SPLITTER = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

    public static void main(String[] args) {
        Utility.setUpLogging();
        Properties prop;

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

        if (prop == null) {
            System.out.println("Props missing...");
            return;
        }

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

        long urlKeyNum = df.select("urlkey")
                .distinct()
                .count() + 1;

        System.out.printf("Url-keys: %d%n", urlKeyNum);

        long memberNum = df.select("member_id")
                .distinct()
                .count();

        System.out.printf("Members: %d%n", memberNum);

        Dataset<Row> aggDF = df.groupBy("member_id", "member_name", "group_country", "group_state")
                .agg(collect_list(col("urlkey")).alias("urlkey"));

//        aggDF.show();

        CountVectorizerModel cvModel = new CountVectorizer()
                .setInputCol("urlkey")
                .setOutputCol("feature")
                .setVocabSize((int) urlKeyNum)
                .setMinDF(1)
                .fit(aggDF);

        spark.udf().register("isNoneZeroVector", (Vector v) -> v.numNonzeros() > 0, DataTypes.BooleanType);

        Dataset<Row> vectorizedDF = cvModel.transform(aggDF)
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


//        Dataset<Row> key = vectorizedDF.where("member_id = 192517871");

        Dataset<Row> joined = approximateJoin(model, vectorizedDF, vectorizedDF);

        JavaRDD<SimilarPeople> rdd = toSimilarPeopleRDD(joined, bc);

        saveToCassandra(rdd);

        spark.stop();
    }

    public static Dataset<Row> approximateJoin(MinHashLSHModel model, Dataset<Row> vectorizedDFA, Dataset<Row> vectorizedDFB) {
        Dataset<Row> similarPPL = model
                .approxSimilarityJoin(vectorizedDFA, vectorizedDFB, THRESHOLD, "distance")
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
        return similarPPL;
    }

    public static JavaRDD<SimilarPeople> toSimilarPeopleRDD(Dataset<Row> df, Broadcast<Map<String, String>> bc) {
        JavaRDD<SimilarPeople> rdd = df
                .javaRDD()
                .map(row -> {
                    SimilarPeople s = new SimilarPeople();
                    s.setIdA(row.getLong(0));
                    s.setNameA(row.getString(1));
                    s.setUrlkeyA(row.getList(2));
                    s.setIdB(row.getLong(3));
                    s.setNameB(row.getString(4));
                    s.setUrlkeyB(row.getList(5));
                    s.setCountryB(bc.value().get(row.getString(6).toUpperCase()));
                    if (s.getCountryB() == null) return null;
                    if (row.getString(7) == null) s.setStateB("");
                    else s.setStateB(row.getString(7));
                    s.setDistance(row.getDouble(8));
                    return s;
                }).filter(ppl -> ppl.getCountryB() != null);
        return rdd;
    }

    public static void saveToCassandra(JavaRDD<SimilarPeople> rdd) {
        Map<String, String> fields = new HashMap<>();
        fields.put("idA", "id_a");
        fields.put("nameA", "name_a");
        fields.put("urlkeyA", "urlkey_a");
        fields.put("idB", "id_b");
        fields.put("nameB", "name_b");
        fields.put("urlkeyB", "urlkey_b");
        fields.put("countryB", "country_b");
        fields.put("stateB", "state_b");
        fields.put("distance", "distance");

        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, SIMILAR_PEOPLE_TABLE,
                        CassandraJavaUtil.mapToRow(SimilarPeople.class,
                                fields))
                .saveToCassandra();
    }
}
