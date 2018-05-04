package com.cmpe280.meetup.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarPeople {
    private Integer ida;
    private String namea;
    private Integer idb;
    private String nameb;
    private Double distance;

    @Override
    public String toString() {
        return String.format("%s %s %s %s", ida, namea, idb, nameb);
    }
}

