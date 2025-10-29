package com.railway.dao;

import com.railway.model.Train;
import com.railway.util.DBConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrainDAOImpl implements TrainDAO {

    private static Train map(ResultSet rs) throws SQLException {
        Train t = new Train();
        t.setId(rs.getString("train_id"));
        t.setName(rs.getString("train_name"));
        t.setSource(rs.getString("source"));
        t.setDestination(rs.getString("destination"));
        t.setTotalSeats(rs.getInt("total_seats"));
        t.setAvailableSeats(rs.getInt("available_seats"));
        t.setFare(rs.getDouble("fare"));
        Timestamp ts = rs.getTimestamp("departure_time");
        t.setDepartureTime(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        return t;
    }

    @Override
    public List<Train> getAllTrains() throws SQLException {
        String sql = "SELECT * FROM trains ORDER BY departure_time";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Train> list = new ArrayList<>();
            while (rs.next()) list.add(map(rs));
            return list;
        }
    }

    @Override
    public Train getById(String trainId) throws SQLException {
        String sql = "SELECT * FROM trains WHERE train_id = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public boolean updateAvailableSeats(String trainId, int availableSeats) throws SQLException {
        String sql = "UPDATE trains SET available_seats = ? WHERE train_id = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, availableSeats);
            ps.setString(2, trainId);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public List<Train> searchByRoute(String source, String destination) throws SQLException {
        String sql = "SELECT * FROM trains WHERE source = ? AND destination = ? ORDER BY departure_time";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, source);
            ps.setString(2, destination);
            try (ResultSet rs = ps.executeQuery()) {
                List<Train> list = new ArrayList<>();
                while (rs.next()) list.add(map(rs));
                return list;
            }
        }
    }

    @Override
    public boolean save(Train t) throws SQLException {
        String sql = "INSERT INTO trains(train_id, train_name, source, destination, total_seats, available_seats, fare, departure_time) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getName());
            ps.setString(3, t.getSource());
            ps.setString(4, t.getDestination());
            ps.setInt(5, t.getTotalSeats());
            ps.setInt(6, t.getAvailableSeats());
            ps.setDouble(7, t.getFare());
            ps.setTimestamp(8, Timestamp.valueOf(t.getDepartureTime()));
            return ps.executeUpdate() == 1;
        }
    }
}


