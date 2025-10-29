package com.railway.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a train in the railway reservation system.
 */
public class Train implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String source;
    private String destination;
    private int totalSeats;
    private int availableSeats;
    private double fare;
    private LocalDateTime departureTime;
    
    // Default constructor
    public Train() {}
    
    // Constructor with all parameters
    public Train(String id, String name, String source, String destination, 
                 int totalSeats, int availableSeats, double fare, LocalDateTime departureTime) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.fare = fare;
        this.departureTime = departureTime;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    
    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }
    
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    
    // Utility methods
    public boolean hasAvailableSeats(int requestedSeats) {
        return availableSeats >= requestedSeats;
    }
    
    public void bookSeats(int seats) {
        if (seats > availableSeats) {
            throw new IllegalArgumentException("Cannot book more seats than available");
        }
        this.availableSeats -= seats;
    }
    
    public void cancelSeats(int seats) {
        if (seats + availableSeats > totalSeats) {
            throw new IllegalArgumentException("Cannot cancel more seats than total capacity");
        }
        this.availableSeats += seats;
    }
    
    public String getRoute() {
        return source + " -> " + destination;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Train train = (Train) o;
        return Objects.equals(id, train.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Train{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", fare=" + fare +
                ", departureTime=" + departureTime +
                '}';
    }
}