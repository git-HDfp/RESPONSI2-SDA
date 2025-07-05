package com.logistics.util;

import com.logistics.model.City;
import com.logistics.model.Route;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets; // Import ini
import java.util.List;

public class CsvWriter {

    /**
     * [Implementasi - Simpan ke CSV]
     * Menulis daftar kota ke file CSV. Akan menimpa file yang ada.
     */
    public static void writeCitiesToCsv(String filePath, List<City> cities) {
        String userDir = System.getProperty("user.dir");
        String absoluteFilePath = userDir + java.io.File.separator + filePath;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absoluteFilePath, StandardCharsets.UTF_8))) {
            writer.write("id,name\n");
            for (City city : cities) {
                writer.write(city.getId() + "," + city.getName() + "\n");
            }
            System.out.println(">> Data kota berhasil disimpan ke: " + absoluteFilePath);
        } catch (IOException e) {
            System.err.println("ERROR: Gagal menyimpan data kota ke CSV: " + absoluteFilePath + ". Detail: " + e.getMessage());
            System.err.println("Pastikan aplikasi memiliki izin tulis ke folder proyek.");
        }
    }

    /**
     * [Implementasi - Simpan ke CSV]
     * */
    public static void writeRoutesToCsv(String filePath, List<Route> routes) {
        String userDir = System.getProperty("user.dir");
        String absoluteFilePath = userDir + java.io.File.separator + filePath;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(absoluteFilePath, StandardCharsets.UTF_8))) {
            writer.write("source_id,destination_id,distance\n");
            for (Route route : routes) {
                writer.write(route.getSource().getId() + "," +
                        route.getDestination().getId() + "," +
                        route.getDistance() + "\n");
            }
            System.out.println(">> Data rute berhasil disimpan ke: " + absoluteFilePath);
        } catch (IOException e) {
            System.err.println("ERROR: Gagal menyimpan data rute ke CSV: " + absoluteFilePath + ". Detail: " + e.getMessage());
            System.err.println("Pastikan aplikasi memiliki izin tulis ke folder proyek.");
        }
    }
}