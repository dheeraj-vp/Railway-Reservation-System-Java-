package com.railway.util;

import com.railway.model.Booking;
import com.railway.model.BookingStatus;
import com.railway.model.Train;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookingAnalytics {

    public static List<Booking> confirmed(List<Booking> bookings) {
        return bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.toList());
    }

    public static double totalRevenue(List<Booking> bookings) {
        return bookings.stream().mapToDouble(Booking::getTotalAmount).sum();
    }

    public static Map<String, List<Booking>> groupByTrain(List<Booking> bookings) {
        return bookings.stream().collect(Collectors.groupingBy(b -> b.getTrain().getId()));
    }

    public static List<Map.Entry<String, Long>> topBookedTrains(List<Booking> bookings, int limit) {
        return bookings.stream()
                .collect(Collectors.groupingBy(b -> {
                    String name = b.getTrain().getName();
                    return name != null ? name : b.getTrain().getId();
                }, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public static List<Train> filterAndSortAvailableTrains(List<Train> trains) {
        return trains.stream()
                .filter(t -> t.getAvailableSeats() > 10)
                .sorted(Comparator.comparing(Train::getFare))
                .collect(Collectors.toList());
    }
}


