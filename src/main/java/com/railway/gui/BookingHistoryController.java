package com.railway.gui;

import com.railway.dao.BookingDAO;
import com.railway.dao.BookingDAOImpl;
import com.railway.dao.PassengerDAO;
import com.railway.dao.PassengerDAOImpl;
import com.railway.model.Booking;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookingHistoryController {
    @FXML private TextField passengerIdField;
    @FXML private TextField emailField;
    @FXML private TableView<Booking> table;
    @FXML private TableColumn<Booking, String> idCol;
    @FXML private TableColumn<Booking, String> trainCol;
    @FXML private TableColumn<Booking, String> dateCol;
    @FXML private TableColumn<Booking, Number> ticketsCol;
    @FXML private TableColumn<Booking, String> amountCol;
    @FXML private TableColumn<Booking, String> statusCol;

    private final BookingDAO bookingDAO = new BookingDAOImpl();
    private final PassengerDAO passengerDAO = new PassengerDAOImpl();
    @FXML private TextArea confirmationArea;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        trainCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getTrain() != null ? c.getValue().getTrain().getId() : ""));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
        ticketsCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getNumberOfTickets()));
        amountCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFormattedAmount()));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().toString()));
        
        // Style confirmation area
        if (confirmationArea != null) {
            confirmationArea.getStyleClass().add("confirmation");
            confirmationArea.setEditable(false);
            confirmationArea.setWrapText(true);
        }
    }

    @FXML
    public void handleLoad() {
        String pid = passengerIdField.getText();
        String email = emailField.getText();
        
        if ((pid == null || pid.isBlank()) && (email == null || email.isBlank())) {
            alert("Validation", "Enter Passenger ID or Email", Alert.AlertType.WARNING);
            return;
        }
        try {
            String passengerId = pid;
            if ((pid == null || pid.isBlank()) && !email.isBlank()) {
                // Search by email
                var passenger = passengerDAO.getByEmail(email);
                if (passenger == null) {
                    alert("Not Found", "No passenger found with email: " + email, Alert.AlertType.INFORMATION);
                    return;
                }
                passengerId = passenger.getId();
                passengerIdField.setText(passengerId); // Auto-fill for convenience
            }
            List<Booking> list = bookingDAO.getByPassenger(passengerId);
            table.setItems(FXCollections.observableArrayList(list));
            confirmationArea.clear();
        } catch (SQLException e) {
            alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleViewConfirmation() {
        Booking selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Info", "Select a booking first", Alert.AlertType.INFORMATION);
            return;
        }
        String path = java.nio.file.Paths.get("data", "reports", "confirmation_" + selected.getId() + ".txt").toString();
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Path.of(path));
            confirmationArea.setText(String.join("\n", lines));
        } catch (Exception ex) {
            confirmationArea.setText("Confirmation file not found for booking " + selected.getId());
        }
    }

    @FXML
    public void handleCancelBooking() {
        Booking selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert("Info", "Select a booking to cancel", Alert.AlertType.INFORMATION);
            return;
        }
        if (selected.getStatus() == com.railway.model.BookingStatus.CANCELLED) {
            alert("Info", "Booking already cancelled", Alert.AlertType.INFORMATION);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setHeaderText("Cancel Booking: " + selected.getId());
        confirm.setContentText("Are you sure? This action cannot be undone.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (bookingDAO.cancel(selected.getId())) {
                    alert("Success", "Booking cancelled: " + selected.getId(), Alert.AlertType.INFORMATION);
                    handleLoad(); // Refresh table
                }
            } catch (SQLException e) {
                alert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void alert(String t, String m, Alert.AlertType type) {
        Alert a = new Alert(type); a.setTitle(t); a.setHeaderText(null); a.setContentText(m); a.showAndWait();
    }
}



