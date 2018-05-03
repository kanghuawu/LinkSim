package com.khwu.model.cassandra;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class SimilarPeople implements Serializable {
    private Long idA;
    private Long idB;
    private Double distance;
}
