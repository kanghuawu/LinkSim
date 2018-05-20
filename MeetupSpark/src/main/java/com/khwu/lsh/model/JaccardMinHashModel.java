package com.khwu.lsh.model;

import com.khwu.lsh.distance.JaccardDistance;
import com.khwu.lsh.lsh.MinHashFunction;
import com.khwu.lsh.util.TopNQueue;
import org.apache.spark.HashPartitioner;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.linalg.Vector;
import scala.Tuple2;
import scala.Tuple3;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class JaccardMinHashModel implements Serializable {
    private JaccardDistance distance;
    private List<MinHashFunction> hashFunctions;
    private int numHashes;
    private int signatureLength;
    private int joinParallelism;
    private boolean sample;
    private int numOutputPartitions;
    private int bucketLimit;

    public JaccardMinHashModel(List<MinHashFunction> hashFunctions, int numHashes, int signatureLength, int parallelism,
                               boolean sample, int numOutputPartitions, int limit) {
        this.hashFunctions = hashFunctions;
        this.numHashes = numHashes;
        this.signatureLength = signatureLength;
        this.joinParallelism = parallelism;
        this.sample = sample;
        this.numOutputPartitions = numOutputPartitions;
        this.bucketLimit = limit;
        distance = new JaccardDistance();
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

    public JavaRDD<Tuple3<Long, Long, Double>> getAllNearestNeighbors(JavaPairRDD<Long, Vector> srcItems, int k) {
        return getAllNearestNeighbors(srcItems, srcItems, k);
    }

    public JavaRDD<Tuple3<Long, Long, Double>> getAllNearestNeighbors(JavaPairRDD<Long, Vector> srcItems,
                                                                      JavaPairRDD<Long, Vector> candidatePool,
                                                                      int k) {
        HashPartitioner hashPartitioner = new HashPartitioner(joinParallelism);
        JavaPairRDD<Integer, Tuple2<Long, Vector>> srcItemsExploded = explodeData(transform(srcItems)).partitionBy(hashPartitioner);
        JavaPairRDD<Integer, Tuple2<Long, Vector>> candidatePoolExploded;
        if (srcItems.id() == candidatePool.id()) {
            candidatePoolExploded = srcItemsExploded;
        } else {
            candidatePoolExploded = explodeData(transform(candidatePool)).partitionBy(hashPartitioner);
        }

        return srcItemsExploded.zipPartitions(candidatePoolExploded, (srcIt, candidateIt) -> {
            Map<Long, Vector> itemVectors = new HashMap<>();
            Map<Integer, List<List<Long>>> hashBuckets = new HashMap<>();
            updateBucket(
                    srcIt,
                    itemVectors,
                    hashBuckets,
                    sample,
                    false
            );
            updateBucket(
                    candidateIt,
                    itemVectors,
                    hashBuckets,
                    sample,
                    true
            );
            return new NearestNeighborIterator(hashBuckets.values().iterator(), itemVectors, k);
        }).mapToPair(x -> x)
                .groupByKey()
                .mapValues(candidateIter -> {
                    TopNQueue topN = new TopNQueue(k);
                    candidateIter.forEach(topN::enqueue);
                    return topN.iterator();
                }).flatMap(tu -> {
                    List<Tuple3<Long, Long, Double>> res = new LinkedList<>();
                    Iterator<Tuple2<Long, Double>> it = tu._2;
                    while (it.hasNext()) {
                        Tuple2<Long, Double> cand = it.next();
                        res.add(new Tuple3<>(tu._1, cand._1, cand._2));
                    }
                    return res.iterator();
                })
                .repartition(numOutputPartitions);
    }

    private JavaPairRDD<Long, Tuple2<Vector, List<List<Integer>>>> transform(JavaPairRDD<Long, Vector> data) {
        return data.mapValues(x -> new Tuple2<>(x, getBandedHashes(x)));
    }

    private JavaPairRDD<Integer, Tuple2<Long, Vector>> explodeData(JavaPairRDD<Long, Tuple2<Vector, List<List<Integer>>>> transformedData) {
        return transformedData.flatMap(tu -> {
            Long id = tu._1;
            Vector vector = tu._2._1;
            List<List<Integer>> bandedHashes = tu._2._2;
            List<Tuple2<Integer, Tuple2<Long, Vector>>> res = new LinkedList<>();
            for (int i = 0; i < bandedHashes.size(); i++) {
                int hash = getHashCode(bandedHashes.get(i), i);
                res.add(new Tuple2<>(hash, new Tuple2<>(id, vector)));
            }
            return res.iterator();
        }).mapToPair(x -> x);
    }

    private int getHashCode(List<Integer> hashesWithBucket, int idx) {
        return 31 * (31 + hashesWithBucket.hashCode()) + idx;
    }

    private void updateBucket(Iterator<Tuple2<Integer, Tuple2<Long, Vector>>> itemIt, Map<Long, Vector> itemVector,
                              Map<Integer, List<List<Long>>> hashBuckets, boolean shouldReservoirSample, boolean isCandidatePoolIt) {
        Map<Integer, Integer> numElementsSeen = new HashMap<>();
        int selector = isCandidatePoolIt ? 1 : 0;
        itemIt.forEachRemaining(tu -> {
            Integer h = tu._1;
            Long id = tu._2._1;
            Vector vector = tu._2._2;
            if (hashBuckets.containsKey(h)) {
                if (hashBuckets.get(h).get(selector).size() == bucketLimit) {
                    if (shouldReservoirSample) {
                        numElementsSeen.put(h, numElementsSeen.getOrDefault(h, bucketLimit) + 1);
                        int idx = new Random().nextInt(numElementsSeen.get(h));
                        if (idx < bucketLimit) {
                            hashBuckets.get(h).get(selector).set(idx, id);
                            if (!itemVector.containsKey(id)) {
                                itemVector.put(id, vector);
                            }
                        }
                    }
                } else {
                    hashBuckets.get(h).get(selector).add(id);
                    if (!itemVector.containsKey(id)) {
                        itemVector.put(id, vector);
                    }
                }
            } else {
                if (!isCandidatePoolIt) {
                    List<List<Long>> item = new ArrayList<>();
                    item.add(new ArrayList<>());
                    item.add(new ArrayList<>());
                    item.get(0).add(id);
                    hashBuckets.put(h, item);
                    if (!itemVector.containsKey(id)) {
                        itemVector.put(id, vector);
                    }
                }
            }
        });
    }

    private class NearestNeighborIterator implements Serializable, Iterator<Tuple2<Long, Iterator<Tuple2<Long, Double>>>> {

        private final int numNearestNeighbors;
        private Tuple2<Long, Iterator<Tuple2<Long, Double>>> nextResult;
        private List<List<Long>> currentTuple;
        private int currentIndex;
        private  Map<Long, Vector> itemVectors;
        private Iterator<List<List<Long>>> bucketsIt;


        public NearestNeighborIterator(Iterator<List<List<Long>>> bucketsIt, Map<Long, Vector> itemVectors, int numNearestNeighbors) {
            if (bucketsIt.hasNext()) {
                currentTuple = bucketsIt.next();
            }
            currentIndex = 0;
            this.itemVectors = itemVectors;
            this.numNearestNeighbors = numNearestNeighbors;
            this.bucketsIt = bucketsIt;
            populateNext();
        }

        private void populateNext() {
            boolean done = false;
            while (currentTuple != null && !done) {
                List<List<Long>> x = currentTuple;
                while (currentIndex < x.get(0).size() && !done) {
                    TopNQueue queue = new TopNQueue(numNearestNeighbors);
                    x.get(1).stream().filter(id -> !id.equals(x.get(0).get(currentIndex)))
                            .map(id -> new Tuple2<>(id, distance.compute(itemVectors.get(id), itemVectors.get(x.get(0).get(currentIndex)))))
                            .forEach(queue::enqueue);
                    if (queue.nonEmpty()) {
                        nextResult = new Tuple2<>(x.get(0).get(currentIndex), queue.iterator());
                        done = true;
                    }
                    currentIndex++;
                }
                if (currentIndex == x.get(0).size()) {
                    currentIndex = 0;
                    currentTuple = bucketsIt.hasNext() ? bucketsIt.next() : null;
                }
            }
            if (currentTuple == null && !done) {
                nextResult = null;
            }
        }

        @Override
        public boolean hasNext() {
            return nextResult != null;
        }

        @Override
        public Tuple2<Long, Iterator<Tuple2<Long, Double>>> next() {
            if (nextResult != null) {
                Tuple2<Long, Iterator<Tuple2<Long, Double>>> res = nextResult;
                populateNext();
                return res;
            }
            return null;
        }
    }
}
