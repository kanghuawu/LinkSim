package com.khwu.model.cassandra;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.khwu.model.kafka.rsvp.Reservation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.streaming.api.java.JavaDStream;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.khwu.util.Utility.CASSANDRA_KEYSPACE;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagByUserId implements Serializable  {
    private Long id;
    private String name;
    private String country;
    private String state;
    private String tag;

    public static final  String TAG_BY_USERID = "tag_by_userid";
    private static Map<String, String> tagByUserIdField;

    public static void saveToCassandra(JavaDStream<Reservation> dStream) {
        if (tagByUserIdField == null) {
            tagByUserIdField = new HashMap<>();
            tagByUserIdField.put("id", "id");
            tagByUserIdField.put("name", "name");
            tagByUserIdField.put("country", "country");
            tagByUserIdField.put("state", "state");
            tagByUserIdField.put("tag", "tag");
        }

        JavaDStream<TagByUserId> rdd = dStream.flatMap(rsvp ->
            rsvp.getGroup().getGroupTopics().stream().map(topic -> {
                TagByUserId user = new TagByUserId();
                user.setId(rsvp.getMember().getMemberId());
                user.setName(rsvp.getMember().getMemberName());
                user.setCountry(rsvp.getGroup().getGroupCountry());
                user.setState(rsvp.getGroup().getGroupState() == null ? "" : rsvp.getGroup().getGroupState());
                user.setTag(topic.getUrlKey());
                return user;
            }).iterator()
        ).filter(Objects::nonNull);

        CassandraStreamingJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, TAG_BY_USERID,
                        CassandraJavaUtil.mapToRow(TagByUserId.class, tagByUserIdField))
                .saveToCassandra();
    }
}
