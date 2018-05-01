package com.khwu.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.MapType;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Utility {

    public final static String KAFKA_TOPIC_MEETUP = "meetup";
//    public static String KAFKA_TOPIC = "meetup";
    public final static String CASSANDRA_KEYSPACE = "meetup";

    public static StructType setUpSchema() {
        StructType event = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("event_id", DataTypes.StringType, true),
                DataTypes.createStructField("event_name", DataTypes.StringType, true),
                DataTypes.createStructField("event_url", DataTypes.StringType, true),
                DataTypes.createStructField("time", DataTypes.LongType, true)
        });

        StructType group_topics = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("urlkey", DataTypes.StringType, true),
                DataTypes.createStructField("topic_name", DataTypes.StringType, true)
        });

        StructType group = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("group_city", DataTypes.StringType, true),
                DataTypes.createStructField("group_country", DataTypes.StringType, true),
                DataTypes.createStructField("group_name", DataTypes.StringType, true),
                DataTypes.createStructField("group_id", DataTypes.LongType, true),
                DataTypes.createStructField("group_lon", DataTypes.FloatType, true),
                DataTypes.createStructField("group_lat", DataTypes.FloatType, true),
                DataTypes.createStructField("group_urlname", DataTypes.StringType, true),
                DataTypes.createStructField("group_state", DataTypes.StringType, true),
                DataTypes.createStructField("group_topics", DataTypes.createArrayType(group_topics), true)
        });

        StructType venue = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("venue_name", DataTypes.StringType, true),
                DataTypes.createStructField("lon", DataTypes.FloatType, true),
                DataTypes.createStructField("lat", DataTypes.FloatType, true),
                DataTypes.createStructField("venue_id", DataTypes.LongType, true)
        });

        MapType otherServices = DataTypes.createMapType(DataTypes.StringType,
                DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType));

        StructType member = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("member_id", DataTypes.LongType, true),
                DataTypes.createStructField("photo", DataTypes.StringType, true),
                DataTypes.createStructField("member_name", DataTypes.StringType, true),
                DataTypes.createStructField("other_services", otherServices, true)
        });

        StructType schema = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("event", event, true),
                DataTypes.createStructField("group", group, true),
                DataTypes.createStructField("venue", venue, true),
                DataTypes.createStructField("visibility", DataTypes.StringType, true),
                DataTypes.createStructField("response", DataTypes.StringType, true),
                DataTypes.createStructField("guests", DataTypes.IntegerType, true),
                DataTypes.createStructField("member", member, true),
                DataTypes.createStructField("rsvp_id", DataTypes.LongType, true),
                DataTypes.createStructField("mtime", DataTypes.LongType, true),
        });

        return schema;
    }

    public static void setUpLogging() {
        Logger.getLogger("org").setLevel(Level.WARN);
    }



    private final static String DEBUG_FILE = "../config/debug.txt";
    private final static String PRODUCTION_FILE = "../config/production.txt";
    public final static String PRODUCTION_MODE = "PRODUCTION_MODE";
    public final static String DEBUG_MODE = "DEBUG_MODE";

    public final static String CASSANDRA_HOST = "CASSANDRA_HOST";
    public final static String CASSANDRA_PORT = "CASSANDRA_PORT";
    public final static String DATA_SOURCE = "DATA_SOURCE";
    public final static String KAFKA_SERVERS = "KAFKA_SERVERS";

    public static Properties setUpConfig(String mode) {
        Properties prop = new Properties();
        System.out.println(System.getProperty("user.dir"));
        String file = mode.equals(PRODUCTION_MODE) ? PRODUCTION_FILE : DEBUG_FILE;
        Set<String> names = new HashSet<>(Arrays.asList(
                CASSANDRA_HOST,
                CASSANDRA_PORT,
                DATA_SOURCE,
                KAFKA_SERVERS));

        try (InputStream input = new FileInputStream(file)) {
            prop.load(input);
            if (!names.equals(prop.stringPropertyNames())) {
                System.out.println("Something missing...");
                return null;
            }
            prop.list(System.out);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
