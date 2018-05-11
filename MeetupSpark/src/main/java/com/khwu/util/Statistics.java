package com.khwu.util;

import com.khwu.model.sql.Schema;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.util.Properties;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.explode;

public class Statistics {
    public static void main(String[] args) {
        Utility.setUpLogging();
        Properties prop;

        SparkConf conf = new SparkConf()
                .setAppName("statistics");

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
        System.out.printf("Total Tags: %d%n", df.select(explode(col("group.group_topics.urlkey"))).distinct().count());
        System.out.printf("Total Events: %d%n", df.select("event.event_id").distinct().count());
        System.out.printf("Total Reservations: %d%n", df.select("rsvp_id").distinct().count());
    }
}
