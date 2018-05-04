package com.khwu.streaming.geospark;

import com.khwu.util.Utility;
import com.twitter.chill.KryoSerializer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.datasyslab.geospark.serde.GeoSparkKryoRegistrator;

import java.util.Properties;

public class SpatialSpark {

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
                .setAppName("geo-spark")
                .set("spark.serializer", KryoSerializer.class.getName())
                .set("spark.kryo.registrator", GeoSparkKryoRegistrator.class.getName());

        JavaSparkContext jsc = new JavaSparkContext(conf);

        GeometryFactory geometryFactory = new GeometryFactory();

        Point pointObject = geometryFactory.createPoint(new Coordinate(-84.01, 34.01));
    }
}
