package com.railway.util;

import com.railway.dao.TrainDAO;
import com.railway.dao.TrainDAOImpl;
import com.railway.model.Train;

import java.sql.SQLException;
import java.util.List;

public class DaoVerify {
    public static void main(String[] args) {
        TrainDAO trainDAO = new TrainDAOImpl();
        try {
            List<Train> trains = trainDAO.getAllTrains();
            System.out.println("Trains in DB: " + trains.size());
            for (int i = 0; i < Math.min(trains.size(), 5); i++) {
                Train t = trains.get(i);
                System.out.println(t.getId() + " | " + t.getName() + " | " + t.getSource() + " -> " + t.getDestination());
            }
        } catch (SQLException e) {
            System.out.println("DAO verification failed: " + e.getMessage());
        }
    }
}




