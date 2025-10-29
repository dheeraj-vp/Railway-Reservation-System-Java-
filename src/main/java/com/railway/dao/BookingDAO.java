package com.railway.dao;

import com.railway.model.Booking;

import java.sql.SQLException;
import java.util.List;

public interface BookingDAO {
	boolean save(Booking booking) throws SQLException;
	Booking getById(String bookingId) throws SQLException;
	List<Booking> getByPassenger(String passengerId) throws SQLException;
	List<Booking> getAll() throws SQLException;
	boolean cancel(String bookingId) throws SQLException;
}
