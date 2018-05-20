package com.khwu.model.cassandra;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.khwu.util.Utility.CASSANDRA_KEYSPACE;

@Data
@NoArgsConstructor
public class SimilarPeople implements Serializable {
    private Long idA;
    private String nameA;
    private List<String> urlkeyA;
    private Long idB;
    private String nameB;
    private List<String> urlkeyB;
    private String countryB;
    private String stateB;
    private Double distance;

    private static final String SIMILAR_PEOPLE_TABLE = "similar_people";

    private static Map<String, String> similarField;

    public static void saveToCassandra(Dataset<Row> df, Broadcast<Map<String, String>> bc) {
        if (similarField == null) {
            similarField = new HashMap<>();
            similarField.put("idA", "id_a");
            similarField.put("nameA", "name_a");
            similarField.put("urlkeyA", "urlkey_a");
            similarField.put("idB", "id_b");
            similarField.put("nameB", "name_b");
            similarField.put("urlkeyB", "urlkey_b");
            similarField.put("countryB", "country_b");
            similarField.put("stateB", "state_b");
            similarField.put("distance", "distance");
        }

        JavaRDD<SimilarPeople> rdd = df
                .javaRDD()
                .map(row -> {
                    SimilarPeople s = new SimilarPeople();
                    s.setIdA(row.getLong(0));
                    s.setNameA(row.getString(1));
                    s.setUrlkeyA(row.getList(2));
                    s.setIdB(row.getLong(3));
                    s.setNameB(row.getString(4));
                    s.setUrlkeyB(row.getList(5));
                    s.setCountryB(bc.value().get(row.getString(6).toUpperCase()));
                    if (s.getCountryB() == null) return null;
                    if (row.getString(7) == null) s.setStateB("");
                    else s.setStateB(row.getString(7));
                    s.setDistance(row.getDouble(8));
                    return s;
                }).filter(ppl -> ppl.getCountryB() != null);

        CassandraJavaUtil.javaFunctions(rdd)
                .writerBuilder(CASSANDRA_KEYSPACE, SIMILAR_PEOPLE_TABLE,
                        CassandraJavaUtil.mapToRow(SimilarPeople.class,
                                similarField))
                .saveToCassandra();
    }
}
