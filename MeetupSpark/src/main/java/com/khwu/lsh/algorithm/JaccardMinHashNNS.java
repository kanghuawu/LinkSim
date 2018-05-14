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
import java.util.List;
import java.util.Random;

@Data
@NoArgsConstructor
public class JaccardMinHashNNS implements Serializable {

    private int numHashes = 256;
    private int signatureLength = 16;
    private int parallelism = 500;
    private boolean sample = false;
    private int numOutputPartitions = 100;
    private int limit = 1000;
    private long seed = 1_000;

//    public JavaRDD<Tuple3<Long, Long, Double>> getSelfAllNearestNeighbors(JavaRDD) {
//
//    }

    public JaccardMinHashModel createModel(int dimension) {
        Random random = new Random(seed);
        List<MinHashFunction> hashes = new ArrayList<>();
        for (int i = 0; i < numHashes / signatureLength; i++) {
            List<Tuple2<Integer, Integer>> func = new ArrayList<>();
            for (int j = 0; j < signatureLength; j++) {
                func.add(new Tuple2<>(1 + random.nextInt(MinHashFunction.LARGE_PRIME - 1),
                        random.nextInt(MinHashFunction.LARGE_PRIME - 1)));
            }
            MinHashFunction minHashFunction = new MinHashFunction(func);
            hashes.add(minHashFunction);
        }
        return new JaccardMinHashModel(hashes);
    }
}
