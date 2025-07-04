package com.logistics.algorithm;

import com.logistics.model.Route;
import java.util.List;
import java.util.Collections;

public class QuickSort {

    /**
     * [Implementasi - Quick Sort: quickSort()]
     */
    public static void quickSortRoutes(List<Route> routes) {
        if (routes == null || routes.isEmpty()) {
            return;
        }
        quickSort(routes, 0, routes.size() - 1);
    }

    private static void quickSort(List<Route> routes, int low, int high) {
        if (low < high) {
            // [Implementasi - Quick Sort: partition() dipanggil di sini]
            int pi = partition(routes, low, high);
            quickSort(routes, low, pi - 1);
            quickSort(routes, pi + 1, high);
        }
    }

    /**
     * [Implementasi - Quick Sort: partition() termasuk operasi swap]
     */
    private static int partition(List<Route> routes, int low, int high) {
        double pivot = routes.get(high).getDistance();
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (routes.get(j).getDistance() <= pivot) {
                i++;
                // [Implementasi - Quick Sort: swap]
                Collections.swap(routes, i, j);
            }
        }
        Collections.swap(routes, i + 1, high);
        return i + 1;
    }
}