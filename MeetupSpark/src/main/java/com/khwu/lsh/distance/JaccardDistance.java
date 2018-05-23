package com.khwu.lsh.distance;

import org.apache.spark.ml.linalg.Vector;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class JaccardDistance implements Serializable {

    public Double compute(Vector x, Vector y) {
        Set<Integer> xSet  = new HashSet<>(changeToList(x.toSparse().indices()));
        Set<Integer> ySet  = new HashSet<>(changeToList(y.toSparse().indices()));
        Set<Integer> intersect = new HashSet<>(xSet);
        intersect.retainAll(ySet);
        Set<Integer> union  = new HashSet<>(xSet);
        union.addAll(ySet);
        return 1.0 - (intersect.size() / (double) union.size());
    }

    private ArrayList<Integer> changeToList(int[] arr) {
        return Arrays.stream(arr).boxed().collect(Collectors.toCollection(ArrayList::new));
    }
}
