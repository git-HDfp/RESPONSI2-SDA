package com.logistics.app;

import com.logistics.model.City;
import com.logistics.model.Graph;
import com.logistics.model.Route;
import com.logistics.algorithm.DisjointSet;
import com.logistics.algorithm.DijkstraShortestPath;
import com.logistics.algorithm.QuickSort;
import com.logistics.algorithm.MergeSort;
import com.logistics.algorithm.BinarySearch;
import com.logistics.algorithm.KruskalMST;
import com.logistics.util.CsvReader;
import com.logistics.util.CsvWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.stream.Collectors;

public class LogisticsManagement {

    private static Graph deliveryGraph;
    private static DisjointSet deliveryZones;
    private static Scanner scanner = new Scanner(System.in);
    private static int nextCityId = 1;

    // [COST] Konstanta Biaya per Kilometer
    private static final double COST_PER_KM = 1200.0;

    public static void main(String[] args) {
        printBanner();
        System.out.println("\nSelamat datang! Memuat data awal...\n");

        deliveryGraph = new Graph();
        deliveryZones = new DisjointSet();

        loadDataFromCsv();

        int choice;
        do {
            clearScreen();
            displayMenu();
            System.out.print("Pilih opsi: ");
            choice = getUserInputInt();

            System.out.println("\n" + "=".repeat(50));
            switch (choice) {
                case 1:
                    addCityManually();
                    break;
                case 2:
                    addRouteManually();
                    break;
                case 3:
                    checkEstimatedRouteCost();
                    break;
                case 4:
                    findOptimalRoute();
                    break;
                case 5:
                    manageDeliveryZones();
                    break;
                case 6:
                    sortRoutesMenu();
                    break;
                case 7:
                    findCityWithBinarySearch();
                    break;
                case 8:
                    findMinimumSpanningTree();
                    break;
                case 9:
                    displayAllCities();
                    break;
                case 10:
                    displayGraphVisualization();
                    break;
                case 11:
                    deliveryZones.printSets(deliveryGraph.getCities());
                    break;
                case 0:
                    System.out.println("Terima kasih telah menggunakan aplikasi logistik! Sampai jumpa.");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Mohon masukkan angka sesuai menu.");
            }
            System.out.println("=".repeat(50) + "\n");
        } while (choice != 0);

        scanner.close();
    }

    private static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final IOException | InterruptedException e) {
        }
    }

    private static void drawFramedTitle(String title) {
        int width = 50;
        int titleLength = title.length();
        int paddingLeft = (width - 2 - titleLength) / 2;
        int paddingRight = width - 2 - titleLength - paddingLeft;

        System.out.println("\n╔" + "═".repeat(width - 2) + "╗");
        System.out.println("║" + " ".repeat(paddingLeft) + title + " ".repeat(paddingRight) + "║");
        System.out.println("╚" + "═".repeat(width - 2) + "╝");
    }

    private static void displayMenu() {
        drawFramedTitle("MENU UTAMA APLIKASI LOGISTIK");
        System.out.println("1. Tambah Kota Baru");
        System.out.println("2. Tambah Rute Pengiriman");
        System.out.println("3. Cek Estimasi Jarak/Biaya Rute");
        System.out.println("4. Cari Rute Tercepat (Shortest Path)");
        System.out.println("5. Kelola Zona Pengiriman (Disjoint Set)");
        System.out.println("6. Urutkan Data (Quick Sort & Merge Sort)");
        System.out.println("7. Cari Kota (Binary Search)");
        System.out.println("8. Temukan Jaringan Biaya Minimum (Minimum Spanning Tree)");
        System.out.println("9. Tampilkan Semua Kota");
        System.out.println("10. Tampilkan Jaringan Kota (Graf)");
        System.out.println("11. Tampilkan Zona Pengiriman");
        System.out.println("0. Keluar Aplikasi");
        System.out.println("------------------------------------");
    }

    private static void loadDataFromCsv() {
        System.out.println("Memuat data kota dan rute dari CSV...");

        // [Implementasi - CsvReader]
        List<City> cities = CsvReader.readCitiesFromCsv("cities.csv");
        AtomicInteger maxCityId = new AtomicInteger(0);
        cities.forEach(city -> {
            deliveryGraph.addCity(city);
            deliveryZones.makeSet(city.getId());
            if (city.getId() > maxCityId.get()) { // [Implementasi - Lanjutkan ID]
                maxCityId.set(city.getId());
            }
        });
        nextCityId = maxCityId.get() + 1;

        System.out.println(">> " + cities.size() + " kota berhasil dimuat dari CSV. ID kota berikutnya akan dimulai dari: " + nextCityId);

        // [Implementasi - CsvReader]
        List<Route> routes = CsvReader.readRoutesFromCsv("routes.csv", deliveryGraph.getCities());
        routes.forEach(route ->
                deliveryGraph.addRoute(route.getSource(), route.getDestination(), route.getDistance())
        );
        System.out.println(">> " + routes.size() + " rute unik berhasil dimuat dari CSV (setiap rute akan ditambahkan dua arah di graf).");
    }

    /**
     * [Implementasi - Simpan ke CSV]
     * Menyimpan data kota dan rute yang ada saat ini ke file CSV.
     */
    private static void saveDataToCsv() {
        drawFramedTitle("MENYIMPAN DATA");
        System.out.println("Memulai proses penyimpanan data...");
        List<City> allCities = new ArrayList<>(deliveryGraph.getCities().values());
        // [Implementasi - Simpan ke CSV]
        com.logistics.util.CsvWriter.writeCitiesToCsv("resources/cities.csv", allCities);

        List<Route> allRoutes = deliveryGraph.getAllUniqueRoutes();
        // [Implementasi - Simpan ke CSV]
        com.logistics.util.CsvWriter.writeRoutesToCsv("resources/routes.csv", allRoutes);
        System.out.println("Penyimpanan data selesai.");
    }

    private static void addCityManually() {
        drawFramedTitle("TAMBAH KOTA BARU");
        System.out.print("Masukkan nama kota baru: ");
        String cityName = scanner.nextLine();
        City newCity = new City(nextCityId++, cityName);
        deliveryGraph.addCity(newCity);
        // [Implementasi - Disjoint Set]
        deliveryZones.makeSet(newCity.getId());
        System.out.println(">> Kota '" + cityName + "' berhasil ditambahkan dengan ID: " + newCity.getId());
        saveDataToCsv();
    }

    private static void addRouteManually() {
        drawFramedTitle("TAMBAH RUTE PENGIRIMAN");
        System.out.print("Masukkan ID kota asal: ");
        int sourceId = getUserInputInt();
        System.out.print("Masukkan ID kota tujuan: ");
        int destId = getUserInputInt();
        System.out.print("Masukkan jarak rute (km): ");
        double distance = getUserInputDouble();

        City source = deliveryGraph.getCities().get(sourceId);
        City destination = deliveryGraph.getCities().get(destId);

        if (source == null || destination == null) {
            System.err.println("ERROR: ID kota asal atau tujuan tidak ditemukan. Rute gagal ditambahkan.");
            return;
        }

        deliveryGraph.addRoute(source, destination, distance);
        System.out.println(">> Rute berhasil ditambahkan: " + source.getName() + " <-> " + destination.getName() + " (" + distance + " km)");
        saveDataToCsv(); // Panggil saveDataToCsv() setelah menambahkan rute
    }

    private static void checkEstimatedRouteCost() {
        drawFramedTitle("CEK ESTIMASI JARAK/BIAYA RUTE");
        if (deliveryGraph.getCities().isEmpty()) {
            System.out.println("Graf kosong. Mohon tambahkan kota dan rute terlebih dahulu.");
            return;
        }

        System.out.print("Masukkan ID kota keberangkatan: ");
        int sourceId = getUserInputInt();
        System.out.print("Masukkan ID kota tujuan: ");
        int destId = getUserInputInt();

        City sourceCity = deliveryGraph.getCities().get(sourceId);
        City destCity = deliveryGraph.getCities().get(destId);

        if (sourceCity == null || destCity == null) {
            System.err.println("ERROR: ID kota keberangkatan atau tujuan tidak valid.");
            return;
        }

        // [Implementasi - Dijkstra Shortest Path]
        DijkstraShortestPath dijkstra = new DijkstraShortestPath();
        dijkstra.findShortestPaths(deliveryGraph, sourceId);

        Double estimatedDistance = dijkstra.distances.get(destId);

        if (estimatedDistance == null || estimatedDistance == Double.POSITIVE_INFINITY) {
            System.out.println(">> Tidak ada estimasi rute yang ditemukan dari " + sourceCity.getName() +
                    " ke " + destCity.getName() + ".");
        } else {
            double estimatedCost = estimatedDistance * COST_PER_KM;
            System.out.printf(">> Estimasi Jarak dari %s ke %s: %.2f km%n",
                    sourceCity.getName(), destCity.getName(), estimatedDistance);
            System.out.printf(">> Estimasi Biaya Pengiriman: Rp%,.2f%n", estimatedCost);
        }
    }


    private static void findOptimalRoute() {
        drawFramedTitle("CARI RUTE TERCEPAT (SHORTEST PATH)");
        if (deliveryGraph.getCities().isEmpty()) {
            System.out.println("Graf kosong. Mohon tambahkan kota dan rute terlebih dahulu.");
            return;
        }

        System.out.print("Masukkan ID kota keberangkatan: ");
        int sourceId = getUserInputInt();
        System.out.print("Masukkan ID kota tujuan: ");
        int destId = getUserInputInt();

        City sourceCity = deliveryGraph.getCities().get(sourceId);
        City destCity = deliveryGraph.getCities().get(destId);

        if (sourceCity == null || destCity == null) {
            System.err.println("ERROR: ID kota keberangkatan atau tujuan tidak valid.");
            return;
        }

        // [Implementasi - Dijkstra Shortest Path]
        DijkstraShortestPath dijkstra = new DijkstraShortestPath();
        dijkstra.findShortestPaths(deliveryGraph, sourceId);

        // [Implementasi - Dijkstra Shortest Path]
        List<City> path = dijkstra.getShortestPath(deliveryGraph, destId);
        Double totalDistance = dijkstra.distances.get(destId);

        if (path == null || path.isEmpty() || totalDistance == Double.POSITIVE_INFINITY) {
            System.out.println(">> Tidak ada rute yang ditemukan dari " + sourceCity.getName() +
                    " ke " + destCity.getName() + ".");
        } else {
            double totalCost = totalDistance * COST_PER_KM;
            System.out.println(">> Rute Optimal Ditemukan:");
            System.out.print("   ");
            for (int i = 0; i < path.size(); i++) {
                System.out.print(path.get(i).getName());
                if (i < path.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.printf("\n>> Total Jarak Terpendek: %.2f km%n", totalDistance);
            System.out.printf(">> Total Biaya Pengiriman: Rp%,.2f%n", totalCost);
        }
    }

    private static void manageDeliveryZones() {
        drawFramedTitle("KELOLA ZONA PENGIRIMAN (DISJOINT SET)");
        System.out.println("Fungsi ini memungkinkan Anda mengelompokkan kota-kota ke dalam zona pengiriman.");
        System.out.println("Ini berguna untuk manajemen wilayah atau perencanaan pusat distribusi.");
        System.out.println("1. Gabungkan Zona Kota");
        System.out.println("2. Cek Apakah Dua Kota dalam Zona yang Sama");
        System.out.println("3. Tampilkan Semua Zona");
        System.out.print("Pilih opsi zona: ");
        int zoneChoice = getUserInputInt();

        switch (zoneChoice) {
            case 1:
                System.out.print("Masukkan ID kota pertama untuk digabung zonanya: ");
                int cityId1 = getUserInputInt();
                System.out.print("Masukkan ID kota kedua untuk digabung zonanya: ");
                int cityId2 = getUserInputInt();

                if (deliveryGraph.getCities().containsKey(cityId1) && deliveryGraph.getCities().containsKey(cityId2)) {
                    // [Implementasi - Disjoint Set]
                    boolean merged = deliveryZones.unionSets(cityId1, cityId2);
                    if (merged) {
                        System.out.println(">> Zona kota " + deliveryGraph.getCities().get(cityId1).getName() +
                                " dan " + deliveryGraph.getCities().get(cityId2).getName() + " berhasil digabungkan.");
                    } else {
                        System.out.println(">> Kota " + deliveryGraph.getCities().get(cityId1).getName() +
                                " dan " + deliveryGraph.getCities().get(cityId2).getName() + " sudah berada dalam zona yang sama.");
                    }
                } else {
                    System.err.println("ERROR: ID kota tidak valid. Penggabungan zona gagal.");
                }
                break;
            case 2:
                System.out.print("Masukkan ID kota pertama: ");
                int checkCityId1 = getUserInputInt();
                System.out.print("Masukkan ID kota kedua: ");
                int checkCityId2 = getUserInputInt();

                if (deliveryGraph.getCities().containsKey(checkCityId1) && deliveryGraph.getCities().containsKey(checkCityId2)) {
                    // [Implementasi - Disjoint Set]
                    boolean sameZone = deliveryZones.areInSameSet(checkCityId1, checkCityId2);
                    System.out.println(">> Kota " + deliveryGraph.getCities().get(checkCityId1).getName() +
                            " dan " + deliveryGraph.getCities().get(checkCityId2).getName() +
                            (sameZone ? " berada dalam zona yang SAMA." : " berada dalam zona yang BERBEDA."));
                } else {
                    System.err.println("ERROR: ID kota tidak valid. Pengecekan zona gagal.");
                }
                break;
            case 3:
                deliveryZones.printSets(deliveryGraph.getCities());
                break;
            default:
                System.out.println("Pilihan tidak valid.");
        }
    }

    private static void sortRoutesMenu() {
        drawFramedTitle("OPSI PENGURUTAN (QUICK SORT & MERGE SORT)");
        System.out.println("1. Urutkan Rute dengan Quick Sort (berdasarkan jarak)");
        System.out.println("2. Urutkan Kota dengan Merge Sort (berdasarkan ID)");
        System.out.print("Pilih metode pengurutan: ");
        int sortChoice = getUserInputInt();

        switch (sortChoice) {
            case 1:
                List<Route> allUniqueRoutesQS = deliveryGraph.getAllUniqueRoutes();
                if (allUniqueRoutesQS.isEmpty()) {
                    System.out.println("Belum ada rute untuk diurutkan.");
                    return;
                }
                System.out.println("\n--- Rute Sebelum Quick Sort ---");
                allUniqueRoutesQS.forEach(System.out::println);
                // [Implementasi - Quick Sort]
                QuickSort.quickSortRoutes(allUniqueRoutesQS);
                System.out.println("\n--- Rute Setelah Quick Sort (Berdasarkan Jarak) ---");
                allUniqueRoutesQS.forEach(System.out::println);
                break;
            case 2:
                List<City> allCitiesMS = new ArrayList<>(deliveryGraph.getCities().values());
                if (allCitiesMS.isEmpty()) {
                    System.out.println("Belum ada kota untuk diurutkan.");
                    return;
                }
                System.out.println("\n--- Kota Sebelum Merge Sort ---");
                allCitiesMS.forEach(System.out::println);
                // [Implementasi - Merge Sort]
                MergeSort.mergeSortCities(allCitiesMS, Comparator.comparing(City::getId));
                System.out.println("\n--- Kota Setelah Merge Sort (Berdasarkan ID) ---");
                allCitiesMS.forEach(System.out::println);
                break;
            default:
                System.out.println("Pilihan tidak valid.");
        }
        System.out.println("-------------------------------------");
    }

    private static void findCityWithBinarySearch() {
        drawFramedTitle("CARI KOTA (BINARY SEARCH)");
        if (deliveryGraph.getCities().isEmpty()) {
            System.out.println("Belum ada kota untuk dicari.");
            return;
        }

        List<City> sortedCities = deliveryGraph.getCities().values().stream()
                .sorted(Comparator.comparing(City::getId))
                .collect(Collectors.toList());

        System.out.print("Masukkan ID kota yang dicari: ");
        int targetId = getUserInputInt();

        // [Implementasi - Binary Search]
        City foundCity = BinarySearch.binarySearchCityById(sortedCities, targetId, Comparator.comparing(City::getId));

        if (foundCity != null) {
            System.out.println(">> Kota ditemukan: " + foundCity);
        } else {
            System.out.println(">> Kota dengan ID " + targetId + " tidak ditemukan.");
        }
    }

    private static void findMinimumSpanningTree() {
        drawFramedTitle("TEMUKAN JARINGAN BIAYA MINIMUM (MST)");
        if (deliveryGraph.getCities().size() < 2) {
            System.out.println("Diperlukan setidaknya 2 kota untuk membangun MST.");
            return;
        }
        if (deliveryGraph.getAllUniqueRoutes().isEmpty()) {
            System.out.println("Belum ada rute yang tersedia untuk membangun MST.");
            return;
        }

        // [Implementasi - Kruskal MST]
        KruskalMST kruskal = new KruskalMST();
        List<Route> mstRoutes = kruskal.findMST(deliveryGraph);

        if (mstRoutes.isEmpty() && deliveryGraph.getCities().size() > 1) {
            System.out.println("Tidak dapat membangun MST. Mungkin graf tidak terhubung atau hanya ada satu kota.");
        } else {
            double totalDistance = 0;
            double totalCost = 0;
            System.out.println(">> Rute yang Membentuk Minimum Spanning Tree (diurutkan berdasarkan jarak):");
            System.out.println("   " + "-".repeat(45));

            // [Implementasi - Kruskal MST] Pastikan terurut untuk tampilan
            mstRoutes.sort(Comparator.comparingDouble((Route route) -> route.getDistance()));

            for (Route route : mstRoutes) {
                double routeCost = route.getDistance() * COST_PER_KM;
                System.out.printf("   - %s (%.2f km, Rp%,.2f)%n", route, route.getDistance(), routeCost);
                totalDistance += route.getDistance();
                totalCost += routeCost;
            }
            System.out.println("   " + "-".repeat(45));
            System.out.printf(">> Total Jarak Minimum Jaringan: %.2f km%n", totalDistance);
            System.out.printf(">> Total Biaya Pembangunan Jaringan: Rp%,.2f%n", totalCost);
        }
    }

    /**
     * [Implementasi - Visualisasi Graph]
     */
    private static void displayGraphVisualization() {
        drawFramedTitle("VISUALISASI JARINGAN KOTA (GRAPH)");
        String graphViz = deliveryGraph.getGraphVisualization();
        if (graphViz.isEmpty()) {
            System.out.println("Graf kosong. Tidak ada yang ditampilkan.");
        } else {
            System.out.println(graphViz);
        }
    }


    private static void displayAllCities() {
        drawFramedTitle("DAFTAR SEMUA KOTA");
        if (deliveryGraph.getCities().isEmpty()) {
            System.out.println("Belum ada kota yang terdaftar.");
            return;
        }
        deliveryGraph.getCities().values().stream()
                .sorted(Comparator.comparing(City::getId))
                .forEach(city -> System.out.println(" - " + city));
    }

    // Helper method untuk input integer yang lebih robust
    private static int getUserInputInt() {
        while (!scanner.hasNextInt()) {
            System.out.println("Input tidak valid. Mohon masukkan angka.");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    // Helper method untuk input double yang lebih robust
    private static double getUserInputDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Input tidak valid. Mohon masukkan angka.");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private static void printBanner(){
        System.out.println("\n" +
                "██╗░░░░░░█████╗░░██████╗░██╗░█████╗░░██████╗  ░██████╗████████╗██╗░█████╗░██╗░░██╗\n" +
                "██║░░░░░██╔══██╗██╔════╝░██║██╔══██╗██╔════╝  ██╔════╝╚══██╔══╝██║██╔══██╗██║░██╔╝\n" +
                "██║░░░░░██║░░██║██║░░██╗░██║██║░░╚═╝╚█████╗░  ╚█████╗░░░░██║░░░██║██║░░╚═╝█████═╝░\n" +
                "██║░░░░░██║░░██║██║░░╚██╗██║██║░░██╗░╚═══██╗  ░╚═══██╗░░░██║░░░██║██║░░██╗██╔═██╗░\n" +
                "███████╗╚█████╔╝╚██████╔╝██║╚█████╔╝██████╔╝  ██████╔╝░░░██║░░░██║╚█████╔╝██║░╚██╗\n" +
                "╚══════╝░╚════╝░░╚═════╝░╚═╝░╚════╝░╚═════╝░  ╚═════╝░░░░╚═╝░░░╚═╝░╚════╝░╚═╝░░╚═╝");
    }
}