package com.khwu.model.cassandra;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.api.java.JavaRDD;

import java.util.HashMap;
import java.util.Map;

import static com.khwu.util.Utility.CASSANDRA_KEYSPACE;

@Data
@NoArgsConstructor
public class UserLocationByState {
    private String state;
    private Long id;
    private Float lat;
    private Float lon;

    public static final  String USER_LOCATION_BY_STATE_TABLE = "user_location_by_state";

    private static Map<String, String> userLocationByStateField;

    public static void saveToCassandra(JavaRDD<UserLocationByState> rdd) {
        if (userLocationByStateField == null) {
            userLocationByStateField = new HashMap<>();
            userLocationByStateField.put("state", "state");
            userLocationByStateField.put("id", "id");
            userLocationByStateField.put("lat", "lat");
            userLocationByStateField.put("lon", "lon");
        }
        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, USER_LOCATION_BY_STATE_TABLE,
                        CassandraJavaUtil.mapToRow(UserLocationByState.class,
                                userLocationByStateField))
                .saveToCassandra();
    }
}
