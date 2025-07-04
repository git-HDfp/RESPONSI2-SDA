package com.logistics.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Graph {
    private Map<Integer, City> cities;
    private Map<Integer, List<Route>> adjList;

    public Graph() {
        this.cities = new HashMap<>();
        this.adjList = new HashMap<>();
    }

    public void addCity(City city) {
        cities.put(city.getId(), city);
        adjList.putIfAbsent(city.getId(), new ArrayList<>());
    }

    public void addRoute(City source, City destination, double distance) {
        if (source == null || destination == null) {
            System.err.println("Error: Kota sumber atau tujuan tidak valid saat menambahkan rute.");
            return;
        }
        if (!cities.containsKey(source.getId()) || !cities.containsKey(destination.getId())) {
            System.err.println("Error: Rute tidak dapat ditambahkan. Kota asal atau tujuan tidak ditemukan di graf.");
            return;
        }

        adjList.get(source.getId()).add(new Route(source, destination, distance));
        adjList.get(destination.getId()).add(new Route(destination, source, distance));
    }

    public Map<Integer, City> getCities() {
        return cities;
    }

    public Map<Integer, List<Route>> getAdjList() {
        return adjList;
    }

    public List<Route> getAllUniqueRoutes() {
        Set<Route> uniqueRoutesSet = new HashSet<>();
        for (List<Route> routes : adjList.values()) {
            uniqueRoutesSet.addAll(routes);
        }
        return new ArrayList<>(uniqueRoutesSet);
    }

    /**
     * [Implementasi - Visualisasi Graph]
     * Mengembalikan representasi String dari graph (adjacency list)
     */
    public String getGraphVisualization() {
        StringBuilder sb = new StringBuilder();
        if (cities.isEmpty()) {
            sb.append("Graf kosong. Belum ada kota.");
            return sb.toString();
        }
        List<City> sortedCities = cities.values().stream()
                .sorted(Comparator.comparing(City::getId))
                .collect(Collectors.toList());

        for (City city : sortedCities) {
            // Tampilkan nama kota dan ID di awal baris
            sb.append(String.format("%-25s (ID: %-3d):%n", city.getName(), city.getId())); // %n untuk newline

            List<Route> routes = adjList.get(city.getId());
            if (routes != null && !routes.isEmpty()) {
                // Urutkan rute berdasarkan nama tujuan untuk tampilan konsisten
                routes.stream()
                        .sorted(Comparator.comparing(r -> r.getDestination().getName()))
                        .forEach(route ->
                                // Tampilkan setiap rute di baris baru dengan indentasi
                                sb.append(String.format("    -> %-15s (%.2f km)%n", route.getDestination().getName(), route.getDistance()))
                        );
            } else {
                sb.append(String.format("    (Tidak ada rute dari kota ini)%n")); // Indentasi juga
            }
            sb.append("\n"); // Baris kosong antar kota untuk pemisah visual
        }
        return sb.toString();
    }
}