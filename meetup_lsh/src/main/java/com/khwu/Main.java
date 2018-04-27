package com.khwu;

import com.khwu.util.Utility;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

public class Main {
    static final String DIR = "/Users/khwu/Downloads/2018-03-29_061608.json";

    public static void main(String[] args) {
        Utility.setUpLogging();

        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("Java Spark SQL basic example")
                .getOrCreate();

        StructType schema = Utility.setUpSchema();

        Dataset<Row> df = spark.read()
                .schema(schema)
                .json(DIR);

        df.select("member", "event").show();
    }
}
