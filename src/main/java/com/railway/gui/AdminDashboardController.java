package com.railway.gui;

import com.railway.dao.*;
import com.railway.model.*;
import com.railway.util.BookingAnalytics;
import com.railway.util.FileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML private VBox contentArea;
    
    private final BookingDAO bookingDAO = new BookingDAOImpl();
    private final TrainDAO trainDAO = new TrainDAOImpl();
    
    @FXML
    public void handleViewAllBookings() {
        contentArea.getChildren().clear();
        try {
            List<Booking> bookings = bookingDAO.getAll();
            TableView<Booking> table = createBookingsTable(bookings);
            Button cancelBtn = new Button("Cancel Selected Booking");
            cancelBtn.setOnAction(e -> handleCancelBooking(table));
            contentArea.getChildren().addAll(new Label("All Bookings (" + bookings.size() + ")"), table, cancelBtn);
        } catch (SQLException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void handleViewAllTrains() {
        contentArea.getChildren().clear();
        try {
            List<Train> trains = trainDAO.getAllTrains();
            TableView<Train> table = createTrainsTable(trains);
            contentArea.getChildren().addAll(new Label("All Trains (" + trains.size() + ")"), table);
        } catch (SQLException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void handleAddTrain() {
        contentArea.getChildren().clear();
        
        Label title = new Label("Add New Train");
        title.getStyleClass().add("section-title");
        
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setPadding(new Insets(16));
        
        // Train ID
        Label idLabel = new Label("Train ID *");
        TextField idField = new TextField();
        idField.setPromptText("e.g., T006");
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        
        // Train Name
        Label nameLabel = new Label("Train Name *");
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Express Train");
        grid.add(nameLabel, 0, 1);
        grid.add(nameField, 1, 1);
        
        // Source
        Label sourceLabel = new Label("Source *");
        TextField sourceField = new TextField();
        sourceField.setPromptText("e.g., Delhi");
        grid.add(sourceLabel, 0, 2);
        grid.add(sourceField, 1, 2);
        
        // Destination
        Label destLabel = new Label("Destination *");
        TextField destField = new TextField();
        destField.setPromptText("e.g., Mumbai");
        grid.add(destLabel, 0, 3);
        grid.add(destField, 1, 3);
        
        // Total Seats
        Label seatsLabel = new Label("Total Seats *");
        TextField seatsField = new TextField();
        seatsField.setPromptText("e.g., 100");
        grid.add(seatsLabel, 0, 4);
        grid.add(seatsField, 1, 4);
        
        // Fare
        Label fareLabel = new Label("Fare (Rs.) *");
        TextField fareField = new TextField();
        fareField.setPromptText("e.g., 1500.00");
        grid.add(fareLabel, 0, 5);
        grid.add(fareField, 1, 5);
        
        // Departure Date & Time
        Label dateLabel = new Label("Departure Date");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusDays(7));
        grid.add(dateLabel, 0, 6);
        grid.add(datePicker, 1, 6);
        
        Label timeLabel = new Label("Departure Time");
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 8);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);
        HBox timeBox = new HBox(4);
        timeBox.getChildren().addAll(hourSpinner, new Label(":"), minuteSpinner);
        hourSpinner.setPrefWidth(60);
        minuteSpinner.setPrefWidth(60);
        grid.add(timeLabel, 0, 7);
        grid.add(timeBox, 1, 7);
        
        // Error label
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error");
        grid.add(errorLabel, 0, 8, 2, 1);
        
        // Buttons
        HBox buttonBox = new HBox(12);
        Button addBtn = new Button("➕ Add Train");
        addBtn.setPrefWidth(150);
        Button clearBtn = new Button("Clear Form");
        clearBtn.setPrefWidth(150);
        clearBtn.getStyleClass().add("button");
        clearBtn.getStyleClass().add("secondary");
        
        clearBtn.setOnAction(e -> {
            idField.clear();
            nameField.clear();
            sourceField.clear();
            destField.clear();
            seatsField.clear();
            fareField.clear();
            datePicker.setValue(LocalDate.now().plusDays(7));
            hourSpinner.getValueFactory().setValue(8);
            minuteSpinner.getValueFactory().setValue(0);
            errorLabel.setText("");
        });
        
        addBtn.setOnAction(e -> {
            errorLabel.setText("");
            try {
                // Validation
                if (idField.getText().trim().isEmpty()) {
                    errorLabel.setText("Train ID is required");
                    return;
                }
                if (nameField.getText().trim().isEmpty()) {
                    errorLabel.setText("Train Name is required");
                    return;
                }
                if (sourceField.getText().trim().isEmpty()) {
                    errorLabel.setText("Source is required");
                    return;
                }
                if (destField.getText().trim().isEmpty()) {
                    errorLabel.setText("Destination is required");
                    return;
                }
                
                int seats;
                double fare;
                try {
                    seats = Integer.parseInt(seatsField.getText().trim());
                    if (seats <= 0) {
                        errorLabel.setText("Seats must be greater than 0");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid seats number");
                    return;
                }
                
                try {
                    fare = Double.parseDouble(fareField.getText().trim());
                    if (fare < 0) {
                        errorLabel.setText("Fare cannot be negative");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    errorLabel.setText("Invalid fare amount");
                    return;
                }
                
                // Check if train ID already exists
                Train existing = trainDAO.getById(idField.getText().trim());
                if (existing != null) {
                    errorLabel.setText("Train ID already exists: " + idField.getText().trim());
                    return;
                }
                
                // Create departure datetime
                LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now().plusDays(7);
                int hour = hourSpinner.getValue() != null ? hourSpinner.getValue() : 8;
                int minute = minuteSpinner.getValue() != null ? minuteSpinner.getValue() : 0;
                java.time.LocalDateTime departure = java.time.LocalDateTime.of(date, java.time.LocalTime.of(hour, minute));
                
                Train t = new Train(idField.getText().trim().toUpperCase(), 
                                  nameField.getText().trim(),
                                  sourceField.getText().trim(),
                                  destField.getText().trim(),
                                  seats, seats, // available = total initially
                                  fare, departure);
                
                if (trainDAO.save(t)) {
                    showAlert("Success", "Train added successfully!\nTrain ID: " + t.getId() + 
                            "\nName: " + t.getName(), Alert.AlertType.INFORMATION);
                    handleViewAllTrains(); // Refresh the trains list
                } else {
                    errorLabel.setText("Failed to save train to database");
                }
            } catch (SQLException ex) {
                errorLabel.setText("Database error: " + ex.getMessage());
            } catch (Exception ex) {
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });
        
        buttonBox.getChildren().addAll(addBtn, clearBtn);
        grid.add(buttonBox, 0, 9, 2, 1);
        
        VBox form = new VBox(16);
        form.getChildren().addAll(title, new Separator(), grid);
        contentArea.getChildren().add(form);
    }
    
    @FXML
    public void handleViewStats() {
        contentArea.getChildren().clear();
        try {
            List<Booking> all = bookingDAO.getAll();
            List<Train> trains = trainDAO.getAllTrains();
            
            long confirmed = BookingAnalytics.confirmed(all).size();
            double revenue = BookingAnalytics.totalRevenue(all);
            long totalPassengers = all.stream()
                .filter(b -> b.getPassenger() != null)
                .map(b -> b.getPassenger().getId())
                .distinct()
                .count();
            
            Label title = new Label("System Statistics");
            title.getStyleClass().add("section-title");
            
            javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
            grid.setHgap(16);
            grid.setVgap(16);
            
            addStatCard(grid, "Total Trains", String.valueOf(trains.size()), 0, 0);
            addStatCard(grid, "Total Bookings", String.valueOf(all.size()), 1, 0);
            addStatCard(grid, "Confirmed", String.valueOf(confirmed), 0, 1);
            addStatCard(grid, "Revenue", "Rs. " + String.format("%.2f", revenue), 1, 1);
            addStatCard(grid, "Passengers", String.valueOf(totalPassengers), 0, 2);
            addStatCard(grid, "Available Seats", String.valueOf(trains.stream().mapToInt(Train::getAvailableSeats).sum()), 1, 2);
            
            VBox stats = new VBox(16);
            stats.getChildren().addAll(title, grid);
            contentArea.getChildren().add(stats);
        } catch (SQLException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void handleRevenueAnalytics() {
        contentArea.getChildren().clear();
        try {
            List<Booking> all = bookingDAO.getAll();
            List<Map.Entry<String, Long>> top = BookingAnalytics.topBookedTrains(all, 5);
            
            VBox analytics = new VBox(8);
            analytics.getChildren().add(new Label("Revenue Analytics"));
            analytics.getChildren().add(new Label("Total Revenue: Rs. " + String.format("%.2f", BookingAnalytics.totalRevenue(all))));
            analytics.getChildren().add(new Label("\nTop 5 Booked Trains:"));
            for (Map.Entry<String, Long> e : top) {
                analytics.getChildren().add(new Label("- " + e.getKey() + ": " + e.getValue() + " bookings"));
            }
            contentArea.getChildren().add(analytics);
        } catch (Exception e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void handleDailyReports() {
        contentArea.getChildren().clear();
        TextArea area = new TextArea();
        area.setPrefRowCount(20);
        area.setWrapText(true);
        
        Button genBtn = new Button("Generate Today's Report");
        genBtn.setOnAction(e -> {
            try {
                LocalDate today = LocalDate.now();
                List<Booking> todayBookings = bookingDAO.getAll().stream()
                    .filter(b -> b.getBookingDate().toLocalDate().equals(today))
                    .collect(Collectors.toList());
                
                String report = com.railway.util.ReportGenerator.formatDailyReport(
                    today.format(DateTimeFormatter.ISO_DATE), todayBookings);
                area.setText(report);
                FileManager.exportDailyReport(today, todayBookings);
                showAlert("Success", "Report generated and saved", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
        contentArea.getChildren().addAll(new Label("Daily Reports"), genBtn, area);
    }
    
    private TableView<Booking> createBookingsTable(List<Booking> bookings) {
        TableView<Booking> table = new TableView<>();
        TableColumn<Booking, String> idCol = new TableColumn<>("Booking ID");
        TableColumn<Booking, String> passengerCol = new TableColumn<>("Passenger");
        TableColumn<Booking, String> trainCol = new TableColumn<>("Train");
        TableColumn<Booking, Number> ticketsCol = new TableColumn<>("Tickets");
        TableColumn<Booking, String> amountCol = new TableColumn<>("Amount");
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        passengerCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getPassenger() != null ? c.getValue().getPassenger().getName() : "N/A"));
        trainCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().getTrain() != null ? c.getValue().getTrain().getName() : "N/A"));
        ticketsCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getNumberOfTickets()));
        amountCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFormattedAmount()));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus().toString()));
        
        table.getColumns().addAll(idCol, passengerCol, trainCol, ticketsCol, amountCol, statusCol);
        table.setItems(FXCollections.observableArrayList(bookings));
        table.setPrefHeight(400);
        return table;
    }
    
    private TableView<Train> createTrainsTable(List<Train> trains) {
        TableView<Train> table = new TableView<>();
        TableColumn<Train, String> idCol = new TableColumn<>("Train ID");
        TableColumn<Train, String> nameCol = new TableColumn<>("Name");
        TableColumn<Train, String> routeCol = new TableColumn<>("Route");
        TableColumn<Train, Number> seatsCol = new TableColumn<>("Available");
        TableColumn<Train, Number> fareCol = new TableColumn<>("Fare");
        
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        routeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRoute()));
        seatsCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableSeats()));
        fareCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getFare()));
        
        table.getColumns().addAll(idCol, nameCol, routeCol, seatsCol, fareCol);
        table.setItems(FXCollections.observableArrayList(trains));
        table.setPrefHeight(400);
        return table;
    }
    
    private void handleCancelBooking(TableView<Booking> table) {
        Booking selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Info", "Select a booking to cancel", Alert.AlertType.INFORMATION);
            return;
        }
        try {
            if (bookingDAO.cancel(selected.getId())) {
                showAlert("Success", "Booking cancelled: " + selected.getId(), Alert.AlertType.INFORMATION);
                handleViewAllBookings();
            }
        } catch (SQLException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void addStatCard(javafx.scene.layout.GridPane grid, String label, String value, int col, int row) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(200);
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        
        Label labelLabel = new Label(label);
        labelLabel.getStyleClass().add("stat-label");
        
        card.getChildren().addAll(valueLabel, labelLabel);
        grid.add(card, col, row);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
