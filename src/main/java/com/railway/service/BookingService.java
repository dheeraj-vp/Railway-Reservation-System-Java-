package com.railway.service;

import com.railway.dao.BookingDAO;
import com.railway.dao.PassengerDAO;
import com.railway.dao.TrainDAO;
import com.railway.exception.InsufficientBalanceException;
import com.railway.exception.InsufficientSeatsException;
import com.railway.exception.InvalidBookingException;
import com.railway.model.Booking;
import com.railway.model.BookingStatus;
import com.railway.model.Passenger;
import com.railway.model.Train;

import java.sql.SQLException;

public class BookingService {

    private final Object trainLock = new Object();
    private final Object paymentLock = new Object();

    private final TrainDAO trainDAO;
    private final PassengerDAO passengerDAO;
    private final BookingDAO bookingDAO;

    public BookingService(TrainDAO trainDAO, PassengerDAO passengerDAO, BookingDAO bookingDAO) {
        this.trainDAO = trainDAO;
        this.passengerDAO = passengerDAO;
        this.bookingDAO = bookingDAO;
    }

    public Booking bookTickets(Passenger passenger, Train train, int tickets)
            throws InvalidBookingException, InsufficientSeatsException, InsufficientBalanceException {

        if (passenger == null || train == null) {
            throw new InvalidBookingException("Passenger and Train must not be null");
        }
        if (tickets <= 0) {
            throw new InvalidBookingException("Tickets must be greater than zero");
        }

        // Lock ordering: always lock train first, then payment
        synchronized (trainLock) {
            // Refresh entities from DB and ensure passenger exists/persisted
            try {
                // Prefer existing passenger by email if present
                if (passenger.getEmail() != null && !passenger.getEmail().isBlank()) {
                    Passenger existing = passengerDAO.getByEmail(passenger.getEmail());
                    if (existing != null) {
                        passenger = existing;
                    } else {
                        // Persist new passenger so wallet update has a row to update
                        passengerDAO.save(passenger);
                    }
                } else {
                    // No email provided, ensure row exists
                    passengerDAO.save(passenger);
                }

                // Refresh latest train state from DB
                Train latest = trainDAO.getById(train.getId());
                if (latest != null) {
                    train = latest;
                }
            } catch (SQLException e) {
                throw new RuntimeException("Pre-booking load failed: " + e.getMessage(), e);
            }
            // Validate seats
            if (train.getAvailableSeats() < tickets) {
                throw new InsufficientSeatsException("Only " + train.getAvailableSeats() + " seats available");
            }

            synchronized (paymentLock) {
                double amount = tickets * train.getFare();
                if (passenger.getWalletBalance() < amount) {
                    throw new InsufficientBalanceException("Insufficient balance");
                }

                try {
                    // Update persistent state first to avoid local inconsistency on crash
                    int newAvail = train.getAvailableSeats() - tickets;
                    boolean seatsUpdated = trainDAO.updateAvailableSeats(train.getId(), newAvail);

                    boolean walletUpdated = passengerDAO.updateWalletBalance(passenger.getId(),
                            passenger.getWalletBalance() - amount);

                    if (!seatsUpdated || !walletUpdated) {
                        throw new RuntimeException("Failed to update seats or wallet");
                    }

                    // Update in-memory objects
                    train.setAvailableSeats(newAvail);
                    passenger.setWalletBalance(passenger.getWalletBalance() - amount);

                    // Save booking
                    Booking booking = new Booking(passenger, train, tickets, amount);
                    booking.setStatus(BookingStatus.CONFIRMED);
                    bookingDAO.save(booking);
                    return booking;

                } catch (SQLException e) {
                    throw new RuntimeException("Booking failed: " + e.getMessage(), e);
                }
            }
        }
    }
}


