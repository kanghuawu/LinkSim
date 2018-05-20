package com.khwu.streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khwu.model.kafka.rsvp.Reservation;
import com.khwu.util.Utility;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.datasyslab.geospark.serde.GeoSparkKryoRegistrator;

import java.util.*;

import static com.khwu.model.cassandra.TagByUserId.saveToCassandra;
import static com.khwu.util.Utility.*;

public class RSVPStreamingMain {

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
                .setAppName("stream-geo-app");

        JavaSparkContext jsc = new JavaSparkContext(conf);

        Map<String, String> param = new HashMap<>();
        param.put("bootstrap.servers", prop.getProperty(KAFKA_SERVERS));
        param.put("zookeeper.connect", prop.getProperty(ZOOKEEPER_SERVERS));

        Set<String> topics = new HashSet<>(Arrays.asList(KAFKA_TOPIC_MEETUP));

        JavaStreamingContext jssc = new JavaStreamingContext(jsc, Durations.seconds(2));

        JavaPairInputDStream<String, String> kafkaStream = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                param,
                topics);

        JavaDStream<Reservation> rsvpRdd = kafkaStream.map(tu ->
            new ObjectMapper().readValue(tu._2, Reservation.class)
        ).filter(rsvp -> rsvp != null && rsvp.getVisibility().equals("public"));

        saveToCassandra(rsvpRdd);

        jssc.start();
        jssc.awaitTermination();
    }
}
