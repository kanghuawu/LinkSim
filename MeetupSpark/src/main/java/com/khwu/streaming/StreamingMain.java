package com.khwu.streaming;

import com.khwu.util.Utility;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.datasyslab.geospark.enums.FileDataSplitter;
import org.datasyslab.geospark.enums.GridType;
import org.datasyslab.geospark.enums.IndexType;
import org.datasyslab.geospark.spatialOperator.JoinQuery;
import org.datasyslab.geospark.spatialRDD.PointRDD;
import org.datasyslab.geospark.spatialRDD.PolygonRDD;
import scala.Tuple2;

import java.util.*;

import static com.khwu.util.Utility.*;

public class StreamingMain {

    public static void main(String[] args) throws InterruptedException {
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
                .setAppName("stream-app");

        JavaSparkContext jsc = new JavaSparkContext(conf);

        PolygonRDD polygonRDD = new PolygonRDD(jsc, prop.getProperty(GEO_DATA),
                FileDataSplitter.GEOJSON, true);

        GeometryFactory geoFactory = new GeometryFactory();

        JavaStreamingContext jssc = new JavaStreamingContext(jsc, Durations.seconds(2));

        Map<String, String> param = new HashMap<>();
        param.put("bootstrap.servers", prop.getProperty(KAFKA_SERVERS));
        param.put("zookeeper.connect", prop.getProperty(ZOOKEEPER_SERVERS));

        Set<String> topics = new HashSet<>(Arrays.asList(KAFKA_TOPIC_GEO));

        JavaPairInputDStream<String, String> kafkaStream = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                param,
                topics);

        kafkaStream.mapToPair(tup -> new Tuple2<>(tup._1, tup._2)).print();

        kafkaStream.map(tup -> tup._2)
                .foreachRDD(rdd -> {
                    JavaRDD<Point> point = rdd.map(record -> {
                        String[] arr = record.split(",");
                        return geoFactory.createPoint(new Coordinate(Double.parseDouble(arr[1]), Double.parseDouble(arr[2])));
                    });

                    if (!rdd.isEmpty()) {
                        PointRDD pointRDD = new PointRDD(point);
                        pointRDD.spatialPartitioning(GridType.QUADTREE);
                        polygonRDD.spatialPartitioning(pointRDD.partitionTree);

                        pointRDD.buildIndex(IndexType.RTREE, true);
                        polygonRDD.spatialPartitionedRDD.persist(StorageLevel.MEMORY_ONLY());

                        long result = JoinQuery.SpatialJoinQuery(pointRDD, polygonRDD, true, false).count();
                        System.out.println(result);
                    }
                });

        jssc.start();
        jssc.awaitTermination();
    }
}
