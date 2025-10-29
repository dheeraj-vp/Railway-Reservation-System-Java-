package com.railway.dao;

import com.railway.model.Train;

import java.sql.SQLException;
import java.util.List;

public interface TrainDAO {
	List<Train> getAllTrains() throws SQLException;
	Train getById(String trainId) throws SQLException;
	boolean updateAvailableSeats(String trainId, int availableSeats) throws SQLException;
	List<Train> searchByRoute(String source, String destination) throws SQLException;
	boolean save(Train train) throws SQLException;
}
