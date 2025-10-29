package com.railway.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController {
    @FXML private TabPane tabPane;

    @FXML
    public void handleOpenSearch(ActionEvent e) {
        openWindow("/fxml/SearchTrainsView.fxml", "Search Trains");
    }

    @FXML
    public void handleOpenHistory(ActionEvent e) {
        openWindow("/fxml/BookingHistoryView.fxml", "Booking History");
    }

    @FXML
    public void handleOpenAdmin(ActionEvent e) {
        openWindow("/fxml/AdminDashboard.fxml", "Admin Dashboard");
    }

    private void openWindow(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.NONE);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}




