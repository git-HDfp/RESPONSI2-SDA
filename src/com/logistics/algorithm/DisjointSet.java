package com.logistics.algorithm;

import com.logistics.model.City;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;

public class DisjointSet {
    private Map<Integer, Integer> parent;
    private Map<Integer, Integer> rank;

    public DisjointSet() {
        parent = new HashMap<>();
        rank = new HashMap<>();
    }

    /**
     * [Implementasi - Disjoint Set: makeSet()]
     */
    public void makeSet(int element) {
        if (!parent.containsKey(element)) {
            parent.put(element, element);
            rank.put(element, 0);
        }
    }

    /**
     * [Implementasi - Disjoint Set: findSet() dengan Path Compression]
     */
    public int findSet(int element) {
        if (!parent.containsKey(element)) {
            makeSet(element);
        }
        if (parent.get(element) != element) {
            parent.put(element, findSet(parent.get(element)));
        }
        return parent.get(element);
    }

    /**
     * [Implementasi - Disjoint Set: unionSets() dengan Union by Rank]
     */
    public boolean unionSets(int element1, int element2) {
        int root1 = findSet(element1);
        int root2 = findSet(element2);

        if (root1 != root2) {
            if (rank.get(root1) < rank.get(root2)) {
                parent.put(root1, root2);
            } else if (rank.get(root1) > rank.get(root2)) {
                parent.put(root2, root1);
            } else {
                parent.put(root2, root1);
                rank.put(root1, rank.get(root1) + 1);
            }
            return true;
        }
        return false;
    }

    public boolean areInSameSet(int element1, int element2) {
        return findSet(element1) == findSet(element2);
    }

    public void printSets(Map<Integer, City> cityMap) {
        Map<Integer, List<City>> representativeToElements = new HashMap<>();
        for (int element : parent.keySet()) {
            int root = findSet(element);
            representativeToElements.putIfAbsent(root, new ArrayList<>());
            City city = cityMap.get(element);
            if (city != null) {
                representativeToElements.get(root).add(city);
            }
        }
        System.out.println("\n--- Status Disjoint Set (Zona Pengiriman) ---");
        if (representativeToElements.isEmpty()) {
            System.out.println("Tidak ada zona yang terbentuk. Mungkin belum ada kota.");
        } else {
            representativeToElements.forEach((root, citiesInZone) -> {
                System.out.print("Zona " + root + ": [ ");
                for (int i = 0; i < citiesInZone.size(); i++) {
                    System.out.print(citiesInZone.get(i).getName());
                    if (i < citiesInZone.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println(" ]");
            });
        }
        System.out.println("----------------------------------------------");
    }
}