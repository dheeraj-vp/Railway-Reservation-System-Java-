package com.railway.util;

import com.railway.dao.*;
import com.railway.model.Booking;
import com.railway.model.Train;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionsSmoke {
    public static void main(String[] args) throws Exception {
        TrainDAO trainDAO = new TrainDAOImpl();
        BookingDAO bookingDAO = new BookingDAOImpl();

        List<Train> trains = trainDAO.getAllTrains();
        TrainScheduleManager schedule = new TrainScheduleManager();
        trains.forEach(schedule::addTrain);

        System.out.println("Routes known: " + schedule.getRouteSet().size());
        System.out.println("Cheapest first: " + schedule.sortTrainsByFare().stream().limit(3).map(Train::getName).toList());

        // Load some bookings for analytics (if any exist)
        List<Booking> bookings = new ArrayList<>();
        try { bookings = bookingDAO.getAll(); } catch (SQLException ignored) {}

        System.out.println("Confirmed count: " + BookingAnalytics.confirmed(bookings).size());
        System.out.println("Total revenue : Rs. " + String.format("%.2f", BookingAnalytics.totalRevenue(bookings)));
        Map<String, List<Booking>> byTrain = BookingAnalytics.groupByTrain(bookings);
        System.out.println("Trains with bookings: " + byTrain.keySet());
        System.out.println("Top trains: " + BookingAnalytics.topBookedTrains(bookings, 3));
        System.out.println("Done.");
    }
}




