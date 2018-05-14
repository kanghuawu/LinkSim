package com.khwu.lsh.model;

import com.khwu.lsh.distance.JaccardDistance;
import com.khwu.lsh.lsh.MinHashFunction;
import org.apache.spark.ml.linalg.Vector;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class JaccardMinHashModel implements Serializable {
    private JaccardDistance distance;
    private List<MinHashFunction> hashFunctions;

    public JaccardMinHashModel(List<MinHashFunction> hashFunctions) {
        this.hashFunctions = hashFunctions;
    }

    public List<MinHashFunction> getHashFunctions() {
        return hashFunctions;
    }

    public List<List<Integer>> getBandedHashes(Vector v) {
        if (v.numNonzeros() <= 0) {
            throw new IllegalArgumentException("Must have at least 1 non zero entry.");
        }
        return hashFunctions.stream().map(func -> func.compute(v)).collect(Collectors.toList());
    }
}
