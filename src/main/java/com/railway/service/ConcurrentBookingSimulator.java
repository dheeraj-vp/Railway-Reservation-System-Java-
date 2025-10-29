package com.railway.service;

import com.railway.dao.*;
import com.railway.exception.InsufficientBalanceException;
import com.railway.exception.InsufficientSeatsException;
import com.railway.exception.InvalidBookingException;
import com.railway.model.Passenger;
import com.railway.model.Train;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

public class ConcurrentBookingSimulator {
    public static void main(String[] args) throws Exception {
        TrainDAO trainDAO = new TrainDAOImpl();
        PassengerDAO passengerDAO = new PassengerDAOImpl();
        BookingDAO bookingDAO = new BookingDAOImpl();
        BookingService bookingService = new BookingService(trainDAO, passengerDAO, bookingDAO);

        Train train = trainDAO.getById("T001");
        if (train == null) {
            System.out.println("Train T001 not found. Load schema first.");
            return;
        }

        int threads = 10;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(exec.submit(() -> {
                Passenger p = new Passenger("P-" + UUID.randomUUID().toString().substring(0, 6), 30,
                        "sim@local", "9999999999", 100000.0);
                try {
                    passengerDAO.save(p);
                } catch (SQLException e) {
                    System.out.println(Thread.currentThread().getName() + ": passenger save failed: " + e.getMessage());
                    return;
                }
                try {
                    bookingService.bookTickets(p, train, 1);
                    System.out.println(Thread.currentThread().getName() + ": booking success");
                } catch (InvalidBookingException | InsufficientSeatsException | InsufficientBalanceException e) {
                    System.out.println(Thread.currentThread().getName() + ": booking failed: " + e.getMessage());
                }
            }));
        }

        for (Future<?> f : futures) {
            try { f.get(); } catch (ExecutionException e) { System.out.println("Task error: " + e.getMessage()); }
        }
        exec.shutdown();

        Train after = trainDAO.getById(train.getId());
        System.out.println("Final available seats for " + after.getId() + ": " + after.getAvailableSeats());
    }
}




