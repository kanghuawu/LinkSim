package com.khwu.util;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
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
import java.util.Properties;

import static com.khwu.util.Utility.CASSANDRA_KEYSPACE;

public class Preprocess {

    private static final String USER_BY_NAME = "user_by_name";

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
                .setAppName("preprocess");

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        StructType schema = Schema.schema();

        String[] files = prop.getProperty(Utility.DATA_SOURCE).split(",");

        Dataset<Row> df = spark.read()
                .schema(schema)
                .json(files);

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
    }
}
