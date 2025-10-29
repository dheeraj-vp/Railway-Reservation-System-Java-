package com.railway.util;

import com.railway.model.Booking;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BookingHistory {
    private final List<Booking> bookingsList = new ArrayList<>();
    private final Map<String, List<Booking>> passengerBookingsMap = new HashMap<>();
    private final TreeSet<Booking> sortedBookings = new TreeSet<>(Comparator.comparing(Booking::getBookingDate));

    public void addBooking(Booking booking) {
        bookingsList.add(booking);
        passengerBookingsMap.computeIfAbsent(booking.getPassenger().getId(), k -> new ArrayList<>()).add(booking);
        sortedBookings.add(booking);
    }

    public List<Booking> getBookingsByPassenger(String passengerId) {
        return passengerBookingsMap.getOrDefault(passengerId, List.of());
    }

    public List<Booking> getBookingsByDate(LocalDate date) {
        return bookingsList.stream()
                .filter(b -> b.getBookingDate().toLocalDate().isEqual(date))
                .collect(Collectors.toList());
    }

    public double calculateTotalRevenue() {
        return bookingsList.stream().mapToDouble(Booking::getTotalAmount).sum();
    }

    public List<Booking> getAll() {
        return Collections.unmodifiableList(bookingsList);
    }
}




