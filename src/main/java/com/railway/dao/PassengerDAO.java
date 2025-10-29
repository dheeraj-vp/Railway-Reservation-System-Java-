package com.railway.dao;

import com.railway.model.Passenger;

import java.sql.SQLException;

public interface PassengerDAO {
	Passenger getById(String id) throws SQLException;
	Passenger getByEmail(String email) throws SQLException;
	boolean save(Passenger passenger) throws SQLException;
	boolean updateWalletBalance(String passengerId, double amount) throws SQLException;
}
