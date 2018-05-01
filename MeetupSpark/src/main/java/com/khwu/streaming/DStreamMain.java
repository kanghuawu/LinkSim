package com.khwu.streaming;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khwu.model.Reservation;
import com.khwu.util.Utility;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import java.util.*;

import static com.khwu.util.Utility.KAFKA_SERVERS;
import static com.khwu.util.Utility.KAFKA_TOPIC_MEETUP;

public class DStreamMain {
    public static void main(String[] args) throws InterruptedException {
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
        if (prop == null) return;

        SparkConf conf = new SparkConf()
                .setMaster(master)
                .setAppName("kafka-dstream");

        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));

        Map<String, String> param = new HashMap<>();
        param.put("bootstrap.servers", prop.getProperty(KAFKA_SERVERS));

        Set<String> topics = new HashSet<>(Arrays.asList(KAFKA_TOPIC_MEETUP));

        JavaPairInputDStream<String, String> kafkaStream = KafkaUtils.createDirectStream(jssc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                param,
                topics);

        kafkaStream.map(tup -> {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(tup._2, Reservation.class);
        })
                .print();

        jssc.start();
        Thread.sleep(10_000);
        jssc.stop(true, true);
    }
}
