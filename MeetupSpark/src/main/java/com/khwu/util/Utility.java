package com.khwu.util;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.khwu.model.cassandra.TagByUserId;
import com.khwu.model.cassandra.UserLocationByState;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.api.java.JavaDStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Utility {

    public static final  String KAFKA_TOPIC_MEETUP = "meetup";
    public static final  String KAFKA_TOPIC_GEO = "geo";
    public static final  String CASSANDRA_KEYSPACE = "meetup";

    public static void setUpLogging() {
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
    }

    private final static String DEBUG_FILE = "../config/debug.txt";
    private final static String PRODUCTION_FILE = "../config/production.txt";
    public final static String PRODUCTION_MODE = "PRODUCTION_MODE";
    public final static String DEBUG_MODE = "DEBUG_MODE";

    public static final String CASSANDRA_HOST = "CASSANDRA_HOST";
    public static final String CASSANDRA_PORT = "CASSANDRA_PORT";
    public static final String DATA_SOURCE = "DATA_SOURCE";
    public static final String KAFKA_SERVERS = "KAFKA_SERVERS";
    public static final String ZOOKEEPER_SERVERS = "ZOOKEEPER_SERVERS";
    public static final String GEO_DATA = "GEO_DATA";
    public static final String COUNTRY_CODE = "COUNTRY_CODE";

    public static Properties setUpConfig(String mode) {
        Properties prop = new Properties();
        System.out.println(System.getProperty("user.dir"));
        String file = mode.equals(PRODUCTION_MODE) ? PRODUCTION_FILE : DEBUG_FILE;
        Set<String> names = new HashSet<>(Arrays.asList(
                CASSANDRA_HOST,
                CASSANDRA_PORT,
                DATA_SOURCE,
                KAFKA_SERVERS,
                ZOOKEEPER_SERVERS,
                GEO_DATA,
                COUNTRY_CODE));

        try (InputStream input = new FileInputStream(file)) {
            prop.load(input);
            if (!names.equals(prop.stringPropertyNames())) {
                System.out.println("Something missing...");
                System.out.println(prop.stringPropertyNames());
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
