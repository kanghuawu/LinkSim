package com.khwu.streaming;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.khwu.model.cassandra.UserLocationByState;
import com.khwu.util.Utility;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.datasyslab.geospark.enums.FileDataSplitter;
import org.datasyslab.geospark.enums.GridType;
import org.datasyslab.geospark.serde.GeoSparkKryoRegistrator;
import org.datasyslab.geospark.spatialOperator.JoinQuery;
import org.datasyslab.geospark.spatialRDD.PointRDD;
import org.datasyslab.geospark.spatialRDD.PolygonRDD;

import java.util.*;
import java.util.stream.Collectors;

import static com.khwu.util.Utility.*;

public class GeoStreamingMain {
    private static String USER_LOCATION_BY_STATE_TABLE = "user_location_by_state";

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
                .set("spark.serializer", KryoSerializer.class.getName())
                .set("spark.kryo.registrator", GeoSparkKryoRegistrator.class.getName())
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

//        kafkaStream.map(tup -> tup._2).print();

        kafkaStream.map(tup -> tup._2)
                .foreachRDD(rdd -> {
                    JavaRDD<Point> point = rdd.map(record -> {
                        String[] arr = record.split(",");
                        Point pt = geoFactory.createPoint(new Coordinate(Double.parseDouble(arr[1]), Double.parseDouble(arr[2])));
                        pt.setUserData(arr[0]);
                        return pt;
                    });


                    if (!rdd.isEmpty()) {
                        PointRDD pointRDD = new PointRDD(point);

                        pointRDD.analyze();
                        polygonRDD.analyze();

                        pointRDD.spatialPartitioning(GridType.QUADTREE);
                        polygonRDD.spatialPartitioning(pointRDD.getPartitioner());

//                        polygonRDD.buildIndex(IndexType.QUADTREE, true);

                        pointRDD.spatialPartitionedRDD.persist(StorageLevel.MEMORY_ONLY());
                        polygonRDD.spatialPartitionedRDD.persist(StorageLevel.MEMORY_ONLY());

                        JavaRDD<Row> geoRdd = JoinQuery.SpatialJoinQuery(pointRDD, polygonRDD, false, false)
                                .flatMap(tup -> {
                                    Iterator<Row> li = tup._2
                                            .stream()
                                            .map(pt -> RowFactory.create(
                                                    tup._1.getUserData().toString().substring(1, 3),
                                                    pt.getUserData(),
                                                    pt.getCoordinate().x,
                                                    pt.getCoordinate().y))
                                            .collect(Collectors.toList()).iterator();
                                    return li;
                                });
//                        System.out.println(geoRdd.collect());
                        JavaRDD<UserLocationByState> geoTypedRdd = toUserLocationByStateRDD(geoRdd);
                        saveToCassandra(geoTypedRdd);
                    }
                });

        jssc.start();
        jssc.awaitTermination();
    }

    private static JavaRDD<UserLocationByState> toUserLocationByStateRDD(JavaRDD<Row> rdd) {
        return rdd
                .map(row -> {
                    UserLocationByState u = new UserLocationByState();
                    u.setState(row.getString(0).toUpperCase());
                    u.setId(Long.parseLong(row.getString(1)));
                    u.setLon((float) row.getDouble(2));
                    u.setLat((float) row.getDouble(3));
                    return u;
                });
    }

    private static void saveToCassandra(JavaRDD<UserLocationByState> rdd) {
        Map<String, String> fields = new HashMap<>();
        fields.put("state", "state");
        fields.put("id", "id");
        fields.put("lat", "lat");
        fields.put("lon", "lon");

        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, USER_LOCATION_BY_STATE_TABLE,
                        CassandraJavaUtil.mapToRow(UserLocationByState.class,
                                fields))
                .saveToCassandra();
    }
}
