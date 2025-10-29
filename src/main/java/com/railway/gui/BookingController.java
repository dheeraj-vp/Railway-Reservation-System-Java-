package com.railway.gui;

import com.railway.dao.*;
import com.railway.exception.InsufficientBalanceException;
import com.railway.exception.InsufficientSeatsException;
import com.railway.exception.InvalidBookingException;
import com.railway.model.Booking;
import com.railway.model.Passenger;
import com.railway.model.Train;
import com.railway.service.BookingService;
import com.railway.util.FileManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BookingController {
    @FXML private Label trainLabel;
    @FXML private TextField nameField;
    @FXML private TextField ageField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Spinner<Integer> ticketsSpinner;
    @FXML private TextField walletField;
    @FXML private Label totalLabel;
    @FXML private Label errorLabel;

    private Train train;
    private final BookingService bookingService = new BookingService(new TrainDAOImpl(), new PassengerDAOImpl(), new BookingDAOImpl());

    @FXML
    public void initialize() {
        // Set up basic real-time validation
        nameField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("[a-zA-Z\\s]+")) {
                nameField.setStyle("-fx-border-color: red;");
            } else {
                nameField.setStyle("");
            }
        });
        emailField.textProperty().addListener((obs, o, n) -> {
            if (!isValidEmail(n)) emailField.setStyle("-fx-border-color: red;"); else emailField.setStyle("");
        });
        phoneField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d{10}")) {
                phoneField.setStyle("-fx-border-color: red;");
            } else {
                phoneField.setStyle("");
            }
        });
        ageField.textProperty().addListener((obs, o, n) -> {
            try {
                int a = Integer.parseInt(n);
                ageField.setStyle((a < 1 || a > 120) ? "-fx-border-color: red;" : "");
            } catch (Exception ex) {
                ageField.setStyle("-fx-border-color: red;");
            }
        });
        walletField.textProperty().addListener((obs, o, n) -> {
            try { Double.parseDouble(n); walletField.setStyle(""); }
            catch (Exception ex) { walletField.setStyle("-fx-border-color: red;"); }
        });
    }

    public void setTrain(Train train) {
        this.train = train;
        trainLabel.setText("Train: " + train.getName() + " (" + train.getId() + ")");
        ticketsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        updateTotal();
        ticketsSpinner.valueProperty().addListener((obs, o, n) -> updateTotal());
    }

    private void updateTotal() {
        int tickets = ticketsSpinner.getValue() == null ? 1 : ticketsSpinner.getValue();
        double total = tickets * (train != null ? train.getFare() : 0.0);
        totalLabel.setText(String.format("Total: Rs. %.2f", total));
    }

    @FXML
    private void handleConfirmBooking() {
        errorLabel.setText("");
        try {
            if (!validateForm()) {
                errorLabel.setText("Please correct highlighted fields.");
                return;
            }
            int age = Integer.parseInt(ageField.getText());
            double wallet = Double.parseDouble(walletField.getText());
            Passenger p = new Passenger(nameField.getText(), age, emailField.getText(), phoneField.getText(), wallet);
            int tickets = ticketsSpinner.getValue();
            Booking booking = bookingService.bookTickets(p, train, tickets);
            FileManager.writeBookingConfirmation(booking);
            // Show confirmation preview with Passenger ID
            String preview = com.railway.util.ReportGenerator.formatBookingConfirmation(booking);
            TextArea area = new TextArea(preview);
            area.setEditable(false);
            area.setWrapText(true);
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Booking Confirmed: " + booking.getId());
            dialog.getDialogPane().setContent(area);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (NumberFormatException e) {
            errorLabel.setText("Invalid numeric input");
        } catch (InvalidBookingException | InsufficientSeatsException | InsufficientBalanceException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        nameField.getScene().getWindow().hide();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private boolean validateForm() {
        boolean ok = true;
        if (!nameField.getText().matches("[a-zA-Z\\s]+")) ok = false;
        try {
            int a = Integer.parseInt(ageField.getText());
            if (a < 1 || a > 120) ok = false;
        } catch (Exception e) { ok = false; }
        if (!isValidEmail(emailField.getText())) ok = false;
        if (!phoneField.getText().matches("\\d{10}")) ok = false;
        try { Double.parseDouble(walletField.getText()); } catch (Exception e) { ok = false; }
        return ok;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}


