package com.railway.dao;

import com.railway.model.Booking;
import com.railway.model.BookingStatus;
import com.railway.model.Passenger;
import com.railway.model.Train;
import com.railway.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {

    private static Booking map(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setId(rs.getString("booking_id"));
        Passenger p = new Passenger();
        p.setId(rs.getString("passenger_id"));
        b.setPassenger(p);
        Train t = new Train();
        t.setId(rs.getString("train_id"));
        b.setTrain(t);
        b.setNumberOfTickets(rs.getInt("number_of_tickets"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        Timestamp ts = rs.getTimestamp("booking_date");
        b.setBookingDate(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        b.setStatus(BookingStatus.valueOf(rs.getString("status")));
        return b;
    }

    @Override
    public boolean save(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings(booking_id, passenger_id, train_id, number_of_tickets, total_amount, booking_date, status) " +
                "VALUES(?,?,?,?,?,CURRENT_TIMESTAMP,?)";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, b.getId());
            ps.setString(2, b.getPassenger().getId());
            ps.setString(3, b.getTrain().getId());
            ps.setInt(4, b.getNumberOfTickets());
            ps.setDouble(5, b.getTotalAmount());
            ps.setString(6, b.getStatus().name());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public Booking getById(String bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public List<Booking> getByPassenger(String passengerId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE passenger_id = ? ORDER BY booking_date DESC";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Booking> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    @Override
    public List<Booking> getAll() throws SQLException {
        String sql = "SELECT * FROM bookings ORDER BY booking_date DESC";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Booking> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    @Override
    public boolean cancel(String bookingId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'CANCELLED' WHERE booking_id = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, bookingId);
            return ps.executeUpdate() == 1;
        }
    }
}


