package com.khwu.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Utility {

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
                DataTypes.createStructField("group_id", DataTypes.IntegerType, true),
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
                DataTypes.createStructField("venue_id", DataTypes.IntegerType, true)
        });

        StructType member = DataTypes.createStructType(new StructField[]{
                DataTypes.createStructField("member_id", DataTypes.IntegerType, true),
                DataTypes.createStructField("photo", DataTypes.StringType, true),
                DataTypes.createStructField("member_name", DataTypes.StringType, true)
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
}
