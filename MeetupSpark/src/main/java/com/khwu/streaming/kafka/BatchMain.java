package com.khwu.streaming.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khwu.model.kafka.rsvp.Reservation;
import com.khwu.util.Utility;
import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.kafka.OffsetRange;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.khwu.util.Utility.KAFKA_SERVERS;
import static com.khwu.util.Utility.KAFKA_TOPIC_MEETUP;

public class BatchMain {
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

        if (prop == null) return;

        SparkConf conf = new SparkConf()
                .setMaster(master)
                .setAppName("kafka-batch-test");

        System.out.println(Arrays.toString(conf.getAll()));
        JavaSparkContext jsc = new JavaSparkContext(conf);

        Map<String, String> param = new HashMap<>();
        param.put("bootstrap.servers", prop.getProperty(KAFKA_SERVERS));

        OffsetRange[] offsetRanges = new OffsetRange[]{
                OffsetRange.create(KAFKA_TOPIC_MEETUP, 0, 0, 10)};

        JavaPairRDD<String, String> pair = KafkaUtils.createRDD(jsc,
                String.class,
                String.class,
                StringDecoder.class,
                StringDecoder.class,
                param,
                offsetRanges);

        JavaRDD<Reservation> rdd = pair.map(tup -> {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(tup._2, Reservation.class);
        });

        rdd.take(10).forEach(System.out::println);
        jsc.stop();
    }
}
