package com.khwu.util;

import com.khwu.model.sql.Schema;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.util.Properties;

public class Statistics {
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
                .setAppName("statistics");

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        StructType schema = Schema.schema();

        String[] files = prop.getProperty(Utility.DATA_SOURCE).split(",");

        Dataset<Row> df = spark.read()
                .schema(schema)
                .json(files);

        System.out.printf("Total Users: %d%n", df.select("member.member_id").distinct().count());
        System.out.printf("Total Groups: %d%n", df.select("group.group_id").distinct().count());
        System.out.printf("Total Tags: %d%n", df.select("group.group_topics.urlkey").distinct().count());
        System.out.printf("Total Events: %d%n", df.select("event.event_id").distinct().count());
        System.out.printf("Total Reservations: %d%n", df.select("rsvp_id").distinct().count());
    }
}
