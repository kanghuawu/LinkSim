package com.khwu.lsh.util;

import scala.Serializable;
import scala.Tuple2;

import java.util.*;

public class TopNQueue implements Serializable {

    private PriorityQueue<Tuple2<Long, Double>> priorityQ;
    private HashSet<Long> elements;
    private final int maxCapacity;

    public TopNQueue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.priorityQ = new PriorityQueue<>((a, b) -> (int) Math.signum(a._2 - b._2));
        this.elements = new HashSet<>();
    }

    public void enqueue(Iterator<Tuple2<Long, Double>> items) {
        items.forEachRemaining(this::enqueue);
    }

    public void enqueue(Tuple2<Long, Double> items) {
        if (!elements.contains(items)) {
            if (priorityQ.size() < maxCapacity) {
                priorityQ.add(items);
                elements.add(items._1);
            } else {
                if (priorityQ.peek()._2 > items._2) {
                    elements.remove(priorityQ.poll()._1);
                    elements.add(items._1);
                    priorityQ.add(items);
                }
            }
        }
    }

    public boolean nonEmpty() {
        return !priorityQ.isEmpty();
    }

    public ArrayList<Tuple2<Long, Double>> iterator() {
        ArrayList<Tuple2<Long, Double>> res = new ArrayList<>();
        while (!priorityQ.isEmpty()) {
            res.add(priorityQ.poll());
        }
        return res;
    }
}
