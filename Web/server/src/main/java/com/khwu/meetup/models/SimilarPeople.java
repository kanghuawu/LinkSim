package com.khwu.meetup.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarPeople {
    @PrimaryKeyColumn(name = "id_a", type = PrimaryKeyType.PARTITIONED)
    private Long idA;

    @Column("name_a")
    private String nameA;

    @Column("urlkey_a")
    private List<String> urlkeyA;

    @PrimaryKeyColumn(name = "id_b", type = PrimaryKeyType.CLUSTERED)
    private Long idB;

    @Column("name_b")
    private String nameB;

    @Column("urlkey_b")
    private List<String> urlkeyB;

    @PrimaryKeyColumn(name = "distance", type = PrimaryKeyType.CLUSTERED)
    private Double distance;
}

