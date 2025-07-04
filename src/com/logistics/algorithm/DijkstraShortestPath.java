package com.logistics.algorithm;

import com.logistics.model.City;
import com.logistics.model.Graph;
import com.logistics.model.Route;

import java.util.*;

public class DijkstraShortestPath {

    public Map<Integer, Double> distances;
    public Map<Integer, Integer> predecessors;

    public DijkstraShortestPath() {
        this.distances = new HashMap<>();
        this.predecessors = new HashMap<>();
    }

    /**
     * [Implementasi - Shortest Path: Dijkstra's Algorithm]
     */
    public void findShortestPaths(Graph graph, int sourceCityId) {
        for (int cityId : graph.getCities().keySet()) {
            distances.put(cityId, Double.POSITIVE_INFINITY);
            predecessors.put(cityId, null);
        }
        distances.put(sourceCityId, 0.0);

        // [Implementasi - Shortest Path: PriorityQueue untuk efisiensi]
        PriorityQueue<Map.Entry<Integer, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        pq.add(new AbstractMap.SimpleEntry<>(sourceCityId, 0.0));

        while (!pq.isEmpty()) {
            Map.Entry<Integer, Double> current = pq.poll();
            int u = current.getKey();
            double distU = current.getValue();

            if (distU > distances.get(u)) {
                continue;
            }

            List<Route> neighbors = graph.getAdjList().get(u);
            if (neighbors != null) {
                for (Route route : neighbors) {
                    int v = route.getDestination().getId();
                    double weight = route.getDistance();

                    if (distances.get(u) + weight < distances.get(v)) {
                        distances.put(v, distances.get(u) + weight);
                        predecessors.put(v, u);
                        pq.add(new AbstractMap.SimpleEntry<>(v, distances.get(v)));
                    }
                }
            }
        }
    }

    /**
     * [Implementasi - Shortest Path: Rekonstruksi Jalur Terpendek]
     */
    public List<City> getShortestPath(Graph graph, int targetCityId) {
        List<City> path = new ArrayList<>();
        Integer current = targetCityId;

        if (distances.get(targetCityId) == null || distances.get(targetCityId) == Double.POSITIVE_INFINITY) {
            return null;
        }

        while (current != null) {
            path.add(0, graph.getCities().get(current));
            current = predecessors.get(current);
        }
        return path;
    }
}