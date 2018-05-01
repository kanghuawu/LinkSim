package com.khwu.cassandra;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SimilarPeople implements Serializable {
    private String nameA;
    private Long idA;
    private String nameB;
    private Long idB;
    private Double distance;
}
