package com.khwu.lsh.lsh;

import org.apache.spark.ml.linalg.Vector;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MinHashFunction implements Serializable {

    public static int LARGE_PRIME = 2038074743;
    private ArrayList<Tuple2<Integer, Integer>> minHashes;

    public MinHashFunction(ArrayList<Tuple2<Integer, Integer>> minHashes) {
        this.minHashes = minHashes;
    }

    public ArrayList<Integer> compute(Vector v) {
        return minHashes.stream().map(tu ->
            Arrays.stream(v.toSparse().indices()).boxed().map(elem ->
                ((1 + elem) * tu._1 + tu._2) % MinHashFunction.LARGE_PRIME
            ).min(Integer::compareTo).get()
        ).collect(Collectors.toCollection(ArrayList::new));
    }
}
