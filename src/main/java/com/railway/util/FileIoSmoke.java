package com.railway.util;

import com.railway.dao.*;
import com.railway.model.*;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FileIoSmoke {
    public static void main(String[] args) throws Exception {
        TrainDAO trainDAO = new TrainDAOImpl();
        PassengerDAO passengerDAO = new PassengerDAOImpl();
        BookingDAO bookingDAO = new BookingDAOImpl();

        Train train = trainDAO.getById("T001");
        if (train == null) {
            System.out.println("Train T001 not found. Ensure schema is loaded.");
            return;
        }

        // Use an existing passenger if possible, else create a lightweight one
        Passenger p;
        try {
            p = passengerDAO.getByEmail("filesmoke@local");
            if (p == null) {
                p = new Passenger("File Smoke", 29, "filesmoke@local", "9000000000", 99999);
                passengerDAO.save(p);
            }
        } catch (SQLException e) {
            System.out.println("Passenger fetch/save failed: " + e.getMessage());
            return;
        }

        // Create a booking object (not committing transactional effects here)
        Booking b = new Booking(p, train, 1, train.getFare());

        // Write confirmation
        FileManager.writeBookingConfirmation(b);
        FileManager.appendBookingHistory(b);

        // Serialize booking and a small list
        Path serPath = Path.of("data", "bookings", "booking_" + b.getId() + ".ser");
        SerializationManager.serializeBooking(b, serPath.toString());
        SerializationManager.serializeBookingList(List.of(b), Path.of("data", "backup", "bookings_backup.ser").toString());

        // Export daily report (with just this booking for demo)
        FileManager.exportDailyReport(LocalDate.now(), List.of(b));

        // Read back one
        Booking back = SerializationManager.deserializeBooking(serPath.toString());
        System.out.println("Deserialized booking ID: " + back.getId());
        System.out.println("Files written under data/ (reports, bookings, backup, passengers).\nDone.");
    }
}




