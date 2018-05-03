package com.khwu.streaming;

import com.khwu.util.Utility;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.StructType;

import java.util.Properties;

import static com.khwu.util.Utility.KAFKA_SERVERS;
import static com.khwu.util.Utility.KAFKA_TOPIC_MEETUP;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;

public class StructureMain {

    public static void main(String[] args) throws StreamingQueryException {
        Utility.setUpLogging();
        Properties prop;
        String master;
        if (args.length > 0) {
            prop = Utility.setUpConfig(args[0]);
            master = args[1];
        } else {
            prop = Utility.setUpConfig(Utility.DEBUG_MODE);
            master = "local[*]";
        }

        SparkConf conf = new SparkConf()
                .setMaster(master)
                .setAppName("meetup-structure-stream");

        SparkSession spark = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        StructType schema = Utility.setUpSchema();

        Dataset<Row> df = spark.readStream()
                .format("kafka")
                .option("bootstrap.servers", KAFKA_SERVERS)
                .option("subscribe", KAFKA_TOPIC_MEETUP)
                .option("startingOffsets", "earliest")
                .load();

        Dataset<Row> meetup = df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
                .select(from_json(col("value"), schema).alias("data"))
                .select("data.*");

        StreamingQuery query = meetup
                .groupBy(col("group.group_country"))
                .count()
                .writeStream()
                .outputMode("complete")
                .format("console")
                .option("truncate", false)
                .start();

        query.awaitTermination();
    }
}