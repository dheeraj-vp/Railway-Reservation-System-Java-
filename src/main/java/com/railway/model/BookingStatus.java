package com.railway.model;

/**
 * Enum representing the status of a booking.
 */
public enum BookingStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    PENDING("Pending");
    
    private final String displayName;
    
    BookingStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}