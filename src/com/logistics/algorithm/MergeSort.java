package com.logistics.algorithm;

import com.logistics.model.City;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class MergeSort {

    /**
     * [Implementasi - Merge Sort: mergeSort()]
     */
    public static void mergeSortCities(List<City> cities, Comparator<City> comparator) {
        if (cities == null || cities.isEmpty() || cities.size() < 2) {
            return;
        }
        mergeSort(cities, new ArrayList<>(cities), 0, cities.size() - 1, comparator);
    }

    private static void mergeSort(List<City> arr, List<City> temp, int left, int right, Comparator<City> comparator) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(arr, temp, left, mid, comparator);
            mergeSort(arr, temp, mid + 1, right, comparator);
            // [Implementasi - Merge Sort: merge() dipanggil di sini]
            merge(arr, temp, left, mid, right, comparator);
        }
    }

    /**
     * [Implementasi - Merge Sort: merge() dengan perbandingan dan penggabungan]
     */
    private static void merge(List<City> arr, List<City> temp, int left, int mid, int right, Comparator<City> comparator) {
        for (int i = left; i <= right; i++) {
            temp.set(i, arr.get(i));
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            // [Implementasi - Merge Sort: Perbandingan dan penggabungan elemen]
            if (comparator.compare(temp.get(i), temp.get(j)) <= 0) {
                arr.set(k++, temp.get(i++));
            } else {
                arr.set(k++, temp.get(j++));
            }
        }

        while (i <= mid) {
            arr.set(k++, temp.get(i++));
        }
    }
}