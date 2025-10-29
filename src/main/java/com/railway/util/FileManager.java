package com.railway.util;

import com.railway.model.Booking;
import com.railway.model.Passenger;
import com.railway.model.Train;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final Path DATA_DIR = Path.of("data");
    private static final Path BOOKINGS_DIR = DATA_DIR.resolve("bookings");
    private static final Path PASSENGERS_DIR = DATA_DIR.resolve("passengers");
    private static final Path REPORTS_DIR = DATA_DIR.resolve("reports");
    private static final Path BACKUP_DIR = DATA_DIR.resolve("backup");

    public static void createDirectories() throws IOException {
        Files.createDirectories(BOOKINGS_DIR);
        Files.createDirectories(PASSENGERS_DIR);
        Files.createDirectories(REPORTS_DIR);
        Files.createDirectories(BACKUP_DIR);
    }

    public static void writeBookingConfirmation(Booking booking) throws IOException {
        createDirectories();
        String fileName = "confirmation_" + booking.getId() + ".txt";
        Path filePath = REPORTS_DIR.resolve(fileName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile(), false))) {
            bw.write(ReportGenerator.formatBookingConfirmation(booking));
        }
    }

    public static List<String> readBookingHistory(String passengerId) throws IOException {
        createDirectories();
        Path historyPath = PASSENGERS_DIR.resolve("history_" + passengerId + ".txt");
        if (!Files.exists(historyPath)) return List.of();
        return Files.readAllLines(historyPath);
    }

    public static void appendBookingHistory(Booking booking) throws IOException {
        createDirectories();
        Path historyPath = PASSENGERS_DIR.resolve("history_" + booking.getPassenger().getId() + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(historyPath.toFile(), true))) {
            bw.write(booking.toString());
            bw.newLine();
        }
    }

    public static void exportDailyReport(LocalDate date, List<Booking> bookings) throws IOException {
        createDirectories();
        String day = date.format(DateTimeFormatter.ISO_DATE);
        Path report = REPORTS_DIR.resolve("daily_bookings_" + day + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(report.toFile(), false))) {
            bw.write(ReportGenerator.formatDailyReport(day, bookings));
        }
    }

    public static void backupBookings(List<Booking> bookings) throws IOException {
        createDirectories();
        Path backup = BACKUP_DIR.resolve("bookings_backup.ser");
        SerializationManager.serializeBookingList(bookings, backup.toString());
    }
}




