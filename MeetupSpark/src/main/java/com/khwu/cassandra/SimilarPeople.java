package com.khwu.cassandra;

import java.io.Serializable;

public class SimilarPeople implements Serializable {
    private Integer ida;
    private String namea;
    private Integer idb;
    private String nameb;
    private Double distance;

    public SimilarPeople() {}

    public Integer getIda() {
        return ida;
    }

    public void setIda(Integer ida) {
        this.ida = ida;
    }

    public String getNamea() {
        return namea;
    }

    public void setNamea(String namea) {
        this.namea = namea;
    }

    public Integer getIdb() {
        return idb;
    }

    public void setIdb(Integer idb) {
        this.idb = idb;
    }

    public String getNameb() {
        return nameb;
    }

    public void setNameb(String nameb) {
        this.nameb = nameb;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", ida, namea, idb, nameb);
    }
}
