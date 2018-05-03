package com.khwu.util;

import com.khwu.model.sql.Schema;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
    public final static String KAFKA_TOPIC_GEO = "geo";
    public final static String CASSANDRA_KEYSPACE = "meetup";

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
    public final static String ZOOKEEPER_SERVERS = "ZOOKEEPER_SERVERS";
    public final static String GEO_DATA = "GEO_DATA";

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
                GEO_DATA));

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
