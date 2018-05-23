package com.khwu.lsh.algorithm;

import com.khwu.lsh.lsh.MinHashFunction;
import com.khwu.lsh.model.JaccardMinHashModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;
import scala.Tuple3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@NoArgsConstructor
public class JaccardMinHashNNS implements Serializable {

    private int numHashes = 256;
    private int signatureLength = 16;
    private int parallelism = 500;
    private boolean sample = false;
    private int numOutputPartitions = 100;
    private int limit = 1000;
    private long seed = 1_000;

    public JaccardMinHashNNS setNumHashes(int numHashes) {
        this.numHashes = numHashes;
        return this;
    }

    public JaccardMinHashNNS setSignatureLength(int signatureLength) {
        this.signatureLength = signatureLength;
        return this;
    }

    public JaccardMinHashNNS setJoinParallelism(int parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public JaccardMinHashNNS setShouldSampleBuckets(boolean sample) {
        this.sample = sample;
        return this;
    }

    public JaccardMinHashNNS setNumOutputPartitions(int numOutputPartitions) {
        this.numOutputPartitions = numOutputPartitions;
        return this;
    }

    public JaccardMinHashNNS setBucketLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public JaccardMinHashNNS setSeed(long seed) {
        this.seed = seed;
        return this;
    }

    public JaccardMinHashModel createModel(int dimension) {
        Random random = new Random(seed);
        ArrayList<MinHashFunction> hashes = new ArrayList<>();
        for (int i = 0; i < numHashes / signatureLength; i++) {
            ArrayList<Tuple2<Integer, Integer>> func = new ArrayList<>();
            for (int j = 0; j < signatureLength; j++) {
                func.add(new Tuple2<>(1 + random.nextInt(MinHashFunction.LARGE_PRIME - 1),
                        random.nextInt(MinHashFunction.LARGE_PRIME - 1)));
            }
            MinHashFunction minHashFunction = new MinHashFunction(func);
            hashes.add(minHashFunction);
        }
        return new JaccardMinHashModel(hashes, numHashes, signatureLength, parallelism, sample, numOutputPartitions, limit);
    }
}
