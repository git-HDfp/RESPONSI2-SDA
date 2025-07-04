package com.logistics.algorithm;

import com.logistics.model.City;
import com.logistics.model.Graph;
import com.logistics.model.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class KruskalMST {

    /**
     * [Implementasi - Minimum Spanning Tree: Kruskal's Algorithm]
     */
    public List<Route> findMST(Graph graph) {
        List<Route> mst = new ArrayList<>();
        List<Route> allRoutes = graph.getAllUniqueRoutes();

        // [Implementasi - MST: Urutkan Edge berdasarkan bobot]
        allRoutes.sort(Comparator.comparingDouble(Route::getDistance));

        DisjointSet ds = new DisjointSet();
        // [Implementasi - MST: Inisialisasi Disjoint Set untuk setiap kota]
        for (Map.Entry<Integer, City> entry : graph.getCities().entrySet()) {
            ds.makeSet(entry.getKey());
        }

        int edgesInMST = 0;
        int numCities = graph.getCities().size();

        for (Route route : allRoutes) {
            int sourceId = route.getSource().getId();
            int destId = route.getDestination().getId();

            // [Implementasi - MST: Gunakan Union-Find untuk deteksi siklus dan penggabungan]
            if (ds.findSet(sourceId) != ds.findSet(destId)) {
                ds.unionSets(sourceId, destId);
                mst.add(route);
                edgesInMST++;

                if (edgesInMST == numCities - 1) {
                    break;
                }
            }
        }
        return mst;
    }
}