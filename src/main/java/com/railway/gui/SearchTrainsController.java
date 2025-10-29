package com.railway.gui;

import com.railway.dao.TrainDAO;
import com.railway.dao.TrainDAOImpl;
import com.railway.model.Train;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.List;

public class SearchTrainsController {
    @FXML private TextField sourceField;
    @FXML private TextField destinationField;
    @FXML private TableView<Train> table;
    @FXML private TableColumn<Train, String> idCol;
    @FXML private TableColumn<Train, String> nameCol;
    @FXML private TableColumn<Train, String> routeCol;
    @FXML private TableColumn<Train, Number> fareCol;
    @FXML private TableColumn<Train, Number> seatsCol;
    @FXML private TableColumn<Train, Void> actionCol;
    @FXML private Label resultsCountLabel;

    private final TrainDAO trainDAO = new TrainDAOImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        nameCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getName()));
        routeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRoute()));
        fareCol.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getFare()));
        seatsCol.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getAvailableSeats()));
        addActionButton();
        
        // Load all trains by default
        loadAllTrains();
    }
    
    private void loadAllTrains() {
        try {
            List<Train> allTrains = trainDAO.getAllTrains();
            ObservableList<Train> data = FXCollections.observableArrayList(allTrains);
            table.setItems(data);
            updateResultsCount(allTrains.size());
        } catch (SQLException ex) {
            showAlert("Error", "Failed to load trains: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleSearchTrains(ActionEvent e) {
        String src = sourceField.getText();
        String dst = destinationField.getText();
        try {
            List<Train> results = (src == null || src.isBlank() || dst == null || dst.isBlank())
                    ? trainDAO.getAllTrains()
                    : trainDAO.searchByRoute(src, dst);
            ObservableList<Train> data = FXCollections.observableArrayList(results);
            table.setItems(data);
            updateResultsCount(results.size());
        } catch (SQLException ex) {
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    public void handleShowAll(ActionEvent e) {
        sourceField.clear();
        destinationField.clear();
        loadAllTrains();
    }
    
    private void updateResultsCount(int count) {
        if (resultsCountLabel != null) {
            resultsCountLabel.setText("(" + count + " train" + (count != 1 ? "s" : "") + " found)");
            resultsCountLabel.getStyleClass().add("results-count");
        }
    }

    private void addActionButton() {
        actionCol.setCellFactory(new Callback<>() {
            @Override public TableCell<Train, Void> call(TableColumn<Train, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Book");
                    { btn.setOnAction(e -> handleBook(getTableView().getItems().get(getIndex()))); }
                    @Override protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });
    }

    private void handleBook(Train train) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/BookingView.fxml"));
            javafx.scene.Parent root = loader.load();
            BookingController controller = loader.getController();
            controller.setTrain(train);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Make Booking");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception ex) {
            showAlert("Error", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}


