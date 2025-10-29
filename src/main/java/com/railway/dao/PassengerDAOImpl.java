package com.railway.dao;

import com.railway.model.Passenger;
import com.railway.util.DBConnectionManager;

import java.sql.*;

public class PassengerDAOImpl implements PassengerDAO {

    private static Passenger map(ResultSet rs) throws SQLException {
        Passenger p = new Passenger();
        p.setId(rs.getString("passenger_id"));
        p.setName(rs.getString("name"));
        p.setAge(rs.getInt("age"));
        p.setEmail(rs.getString("email"));
        p.setPhone(rs.getString("phone"));
        p.setWalletBalance(rs.getDouble("wallet_balance"));
        return p;
    }

    @Override
    public Passenger getById(String id) throws SQLException {
        String sql = "SELECT * FROM passengers WHERE passenger_id = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public Passenger getByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM passengers WHERE email = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public boolean save(Passenger p) throws SQLException {
        String sql = "INSERT INTO passengers(passenger_id, name, age, email, phone, wallet_balance) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getName());
            ps.setInt(3, p.getAge());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getPhone());
            ps.setDouble(6, p.getWalletBalance());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updateWalletBalance(String passengerId, double amount) throws SQLException {
        String sql = "UPDATE passengers SET wallet_balance = ? WHERE passenger_id = ?";
        try (Connection c = DBConnectionManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setString(2, passengerId);
            return ps.executeUpdate() == 1;
        }
    }
}


