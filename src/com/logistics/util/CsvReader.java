package com.logistics.util;

import com.logistics.model.City;
import com.logistics.model.Route;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects; // Tetap diperlukan untuk Objects.requireNonNull

public class CsvReader {

    public static List<City> readCitiesFromCsv(String filePath) {
        List<City> cities = new ArrayList<>();
        try (InputStream is = CsvReader.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {

            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 2) {
                    int id = Integer.parseInt(data[0].trim());
                    String name = data[1].trim();
                    cities.add(new City(id, name));
                }
            }
        } catch (IOException | NumberFormatException | NullPointerException e) {
            System.err.println("Error memuat file CSV: " + filePath + ". Pastikan file ada dan classpath sudah benar.");
            System.err.println("Detail: " + e.getMessage());
            // Baris ini sangat membantu debugging, meskipun bukan "komentar gajelas"
            System.err.println("Classpath saat ini: " + System.getProperty("java.class.path"));
        }
        return cities;
    }

    public static List<Route> readRoutesFromCsv(String filePath, Map<Integer, City> cityMap) {
        List<Route> routes = new ArrayList<>();
        try (InputStream is = CsvReader.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8))) {

            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    int sourceId = Integer.parseInt(data[0].trim());
                    int destId = Integer.parseInt(data[1].trim());
                    double distance = Double.parseDouble(data[2].trim());

                    City source = cityMap.get(sourceId);
                    City destination = cityMap.get(destId);

                    if (source != null && destination != null) {
                        routes.add(new Route(source, destination, distance));
                    }
                }
            }
        } catch (IOException | NumberFormatException | NullPointerException e) {
            System.err.println("Error memuat file CSV: " + filePath + ". Pastikan file ada dan classpath sudah benar.");
            System.err.println("Detail: " + e.getMessage());
            System.err.println("Classpath saat ini: " + System.getProperty("java.class.path"));
        }
        return routes;
    }
}