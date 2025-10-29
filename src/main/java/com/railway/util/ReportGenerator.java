package com.railway.util;

import com.railway.model.Booking;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {

    public static String formatBookingConfirmation(Booking b) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("        BOOKING CONFIRMATION\n");
        sb.append("========================================\n");
        sb.append("Booking ID    : ").append(b.getId()).append('\n');
        sb.append("Passenger     : ").append(b.getPassenger().getName()).append('\n');
        sb.append("Passenger ID  : ").append(b.getPassenger().getId()).append('\n');
        sb.append("Train         : ").append(b.getTrain().getName()).append(" (").append(b.getTrain().getId()).append(")\n");
        sb.append("Route         : ").append(b.getTrain().getSource()).append(" -> ").append(b.getTrain().getDestination()).append('\n');
        sb.append("Tickets       : ").append(b.getNumberOfTickets()).append('\n');
        sb.append("Amount Paid   : ").append(b.getFormattedAmount()).append('\n');
        sb.append("Booking Date  : ").append(b.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append('\n');
        sb.append("Status        : ").append(b.getStatus()).append('\n');
        sb.append("========================================\n");
        return sb.toString();
    }

    public static String formatDailyReport(String day, List<Booking> bookings) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== Daily Booking Summary - ").append(day).append(" =====\n");
        sb.append("Total bookings: ").append(bookings.size()).append('\n');
        double total = bookings.stream().mapToDouble(Booking::getTotalAmount).sum();
        sb.append("Total revenue : Rs. ").append(String.format("%.2f", total)).append('\n');
        sb.append('\n');
        for (Booking b : bookings) {
            sb.append(b.getId()).append(" | ")
              .append(b.getTrain().getName()).append(" | ")
              .append(b.getPassenger().getName()).append(" | ")
              .append(b.getNumberOfTickets()).append(" tickets | ")
              .append(b.getFormattedAmount()).append(" | ")
              .append(b.getStatus()).append('\n');
        }
        return sb.toString();
    }
}


