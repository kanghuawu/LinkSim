package com.khwu.model.cassandra;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
}
