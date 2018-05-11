package com.khwu.util;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.khwu.model.cassandra.TagByUserId;
import com.khwu.model.cassandra.UserByName;
import com.khwu.model.sql.Schema;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import static com.khwu.util.Utility.CASSANDRA_KEYSPACE;
import static com.khwu.util.Utility.TAG_BY_USERID;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.explode;

public class Preprocess {

    private static final String USER_BY_NAME = "user_by_name";

    public static void main(String[] args) {
        Utility.setUpLogging();
        Properties prop;

        SparkConf conf = new SparkConf()
                .setAppName("preprocess");

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
        if (prop == null) {
            System.out.println("Props missing...");
            return;
        }

        conf.set("spark.cassandra.connection.host", prop.getProperty(Utility.CASSANDRA_HOST));
        conf.set("spark.cassandra.connection.port", prop.getProperty(Utility.CASSANDRA_PORT));

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        StructType schema = Schema.schema();

        String[] files = prop.getProperty(Utility.DATA_SOURCE).split(",");

        Dataset<Row> df = spark.read()
                .schema(schema)
                .json(files)
                .cache();

        JavaRDD<UserByName> rdd = df.select("member.member_name", "member.member_id")
                .toJavaRDD()
                .map(row -> {
                    UserByName userByName = new UserByName();
                    userByName.setName(row.getString(0));
                    userByName.setId(row.getLong(1));
                    return userByName;
                });

        Map<String, String> fieldToColumnMapping = new HashMap<>();
        fieldToColumnMapping.put("name", "name");
        fieldToColumnMapping.put("id", "id");

        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, USER_BY_NAME,
                        CassandraJavaUtil.mapToRow(UserByName.class, fieldToColumnMapping))
                .saveToCassandra();

        JavaRDD<TagByUserId> tagRdd = df.select("member.member_id",
                "member.member_name",
                "group.group_country",
                "group.group_state",
                "group.group_topics.urlkey")
                .withColumn("urlkey", explode(col("urlkey")))
                .toJavaRDD()
                .map(row -> {
                    if (row.anyNull()) return null;
                    TagByUserId tagByUserId = new TagByUserId();
                    tagByUserId.setId(row.getLong(0));
                    tagByUserId.setName(row.getString(1));
                    tagByUserId.setCountry(row.getString(2));
                    tagByUserId.setState(row.getString(3));
                    tagByUserId.setTag(row.getString(4));
                    return tagByUserId;
                }).filter(Objects::nonNull);

        Map<String, String> tagFieldToColumnMapping = new HashMap<>();
        fieldToColumnMapping.put("id", "id");
        fieldToColumnMapping.put("name", "name");
        fieldToColumnMapping.put("country", "country");
        fieldToColumnMapping.put("state", "state");
        fieldToColumnMapping.put("tag", "tag");

        CassandraJavaUtil.javaFunctions(tagRdd)
        .writerBuilder(CASSANDRA_KEYSPACE, TAG_BY_USERID,
                CassandraJavaUtil.mapToRow(TagByUserId.class, tagFieldToColumnMapping))
//                .withConstantTTL(1000)
        .saveToCassandra();
    }
}
