package com.logistics.algorithm;

import com.logistics.model.City;
import java.util.List;
import java.util.Comparator;

public class BinarySearch {

    /**
     * [Implementasi - Binary Search: binarySearch()]
     */
    public static City binarySearchCityById(List<City> cities, int targetId, Comparator<City> comparator) {
        if (cities == null || cities.isEmpty()) {
            return null;
        }

        int low = 0;
        int high = cities.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            City midCity = cities.get(mid);

            // [Implementasi - Binary Search: Perbandingan]
            int comparison = comparator.compare(midCity, new City(targetId, ""));

            if (comparison == 0) {
                return midCity;
            } else if (comparison < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }
}