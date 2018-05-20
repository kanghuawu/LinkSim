package com.khwu.lsh.distance;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JaccardDistanceTest {

    JaccardDistance distance;
    @Before
    public void setUp() throws Exception {
        distance = new JaccardDistance();
    }

    @Test
    public void computeSameVector() {
        Vector v1 = Vectors.sparse(4, new int[]{0, 1, 3}, new double[]{1.0, 1.0, 1.0});
        double dis = distance.compute(v1, v1);
        assertEquals(0, dis, 0.001);
    }

    @Test
    public void computeCompleteDifferentVector() {
        Vector v1 = Vectors.sparse(4, new int[]{0, 2}, new double[]{1.0, 1.0});
        Vector v2 = Vectors.sparse(4, new int[]{1, 3}, new double[]{1.0, 1.0});
        double dis = distance.compute(v1, v2);
        assertEquals(1.0, dis, 0.001);
    }

    @Test
    public void computeDifferentVector() {
        Vector v1 = Vectors.sparse(4, new int[]{0, 2}, new double[]{1.0, 1.0});
        Vector v2 = Vectors.sparse(4, new int[]{1, 2, 3}, new double[]{1.0, 1.0, 1.0});
        double dis = distance.compute(v1, v2);
        assertEquals(1 - (1 / (double) 4), dis, 0.001);
    }
}