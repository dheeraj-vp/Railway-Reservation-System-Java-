package com.railway.util;

import com.railway.dao.*;
import com.railway.model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class DbWriteSmoke {
    public static void main(String[] args) {
        PassengerDAO passengerDAO = new PassengerDAOImpl();
        TrainDAO trainDAO = new TrainDAOImpl();
        BookingDAO bookingDAO = new BookingDAOImpl();

        try {
            // 1) Ensure a train exists
            Train train = trainDAO.getById("T001");
            if (train == null) {
                System.out.println("Train T001 not found; aborting write smoke test.");
                return;
            }

            // 2) Insert a passenger
            Passenger passenger = new Passenger("Smoke Tester", 30, "smoke.tester@local", "9999999999", 10000.0);
            boolean pSaved = passengerDAO.save(passenger);
            System.out.println("Passenger saved: " + pSaved + " | ID: " + passenger.getId());

            // 3) Create a booking of 2 tickets
            int tickets = 2;
            double amount = tickets * train.getFare();
            Booking booking = new Booking(passenger, train, tickets, amount);
            booking.setStatus(BookingStatus.CONFIRMED);
            boolean bSaved = bookingDAO.save(booking);
            System.out.println("Booking saved: " + bSaved + " | ID: " + booking.getId());

            // 4) Update train seats and passenger wallet to reflect the booking
            int newAvail = train.getAvailableSeats() - tickets;
            boolean seatsUpdated = trainDAO.updateAvailableSeats(train.getId(), newAvail);
            boolean walletUpdated = passengerDAO.updateWalletBalance(passenger.getId(), passenger.getWalletBalance() - amount);
            System.out.println("Seats updated: " + seatsUpdated + ", Wallet updated: " + walletUpdated);

            // 5) Read back to verify
            Train trainAfter = trainDAO.getById(train.getId());
            System.out.println("Train " + trainAfter.getId() + " available seats after booking: " + trainAfter.getAvailableSeats());
            Booking fetched = bookingDAO.getById(booking.getId());
            System.out.println("Fetched booking status: " + (fetched != null ? fetched.getStatus() : null));

        } catch (SQLException e) {
            System.out.println("Write smoke failed: " + e.getMessage());
        }
    }
}




