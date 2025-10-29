package com.railway.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a booking in the railway reservation system.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private Passenger passenger;
    private Train train;
    private int numberOfTickets;
    private double totalAmount;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    
    // Default constructor
    public Booking() {
        this.id = generateId();
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }
    
    // Constructor with parameters (ID auto-generated)
    public Booking(Passenger passenger, Train train, int numberOfTickets, double totalAmount) {
        this.id = generateId();
        this.passenger = passenger;
        this.train = train;
        this.numberOfTickets = numberOfTickets;
        this.totalAmount = totalAmount;
        this.bookingDate = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }
    
    // Constructor with all parameters including ID
    public Booking(String id, Passenger passenger, Train train, int numberOfTickets, 
                   double totalAmount, LocalDateTime bookingDate, BookingStatus status) {
        this.id = id;
        this.passenger = passenger;
        this.train = train;
        this.numberOfTickets = numberOfTickets;
        this.totalAmount = totalAmount;
        this.bookingDate = bookingDate;
        this.status = status;
    }
    
    // Auto-generate booking ID
    private String generateId() {
        return "B" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    
    public Train getTrain() { return train; }
    public void setTrain(Train train) { this.train = train; }
    
    public int getNumberOfTickets() { return numberOfTickets; }
    public void setNumberOfTickets(int numberOfTickets) { this.numberOfTickets = numberOfTickets; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    
    // Utility methods
    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }
    
    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }
    
    public boolean isPending() {
        return status == BookingStatus.PENDING;
    }
    
    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }
    
    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }
    
    public void setPending() {
        this.status = BookingStatus.PENDING;
    }
    
    public String getFormattedBookingDate() {
        return bookingDate.toString().replace("T", " ");
    }
    
    public String getFormattedAmount() {
        return String.format("Rs. %.2f", totalAmount);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", passenger=" + (passenger != null ? passenger.getName() : "null") +
                ", train=" + (train != null ? train.getName() : "null") +
                ", numberOfTickets=" + numberOfTickets +
                ", totalAmount=" + totalAmount +
                ", bookingDate=" + bookingDate +
                ", status=" + status +
                '}';
    }
}