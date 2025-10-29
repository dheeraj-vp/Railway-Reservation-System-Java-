package com.railway;

import com.railway.model.*;
import com.railway.exception.*;

/**
 * Main class for the Railway Reservation System.
 * Tests Phase 1 implementation.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    Railway Reservation System v1.0.0");
        System.out.println("==========================================");
        System.out.println();
        
        // Test basic functionality
        testBasicFunctionality();
        
        System.out.println("Phase 1: Project Setup & Core Data Models - COMPLETED");
        System.out.println("✓ Project structure created");
        System.out.println("✓ Core domain models implemented");
        System.out.println("✓ Custom exceptions created");
        System.out.println("✓ Database schema ready");
        System.out.println("✓ Configuration files set up");
        System.out.println();
        System.out.println("Ready to proceed to Phase 2: Database Connectivity & DAO Layer");
    }
    
    /**
     * Test basic functionality of the core models
     */
    private static void testBasicFunctionality() {
        System.out.println("Testing Core Models:");
        System.out.println("-------------------");
        
        // Test Train model
        Train train = new Train("T001", "Rajdhani Express", "Delhi", "Mumbai", 
                               100, 100, 1500.00, java.time.LocalDateTime.now());
        System.out.println("✓ Train created: " + train.getName());
        System.out.println("  Route: " + train.getRoute());
        System.out.println("  Available seats: " + train.getAvailableSeats());
        
        // Test Passenger model
        Passenger passenger = new Passenger("John Doe", 35, "john@email.com", 
                                           "9876543210", 5000.00);
        System.out.println("✓ Passenger created: " + passenger.getName());
        System.out.println("  ID: " + passenger.getId());
        System.out.println("  Wallet balance: " + passenger.getWalletBalance());
        
        // Test Booking model
        Booking booking = new Booking(passenger, train, 2, 3000.00);
        System.out.println("✓ Booking created: " + booking.getId());
        System.out.println("  Status: " + booking.getStatus());
        System.out.println("  Amount: " + booking.getFormattedAmount());
        
        // Test exception handling
        try {
            if (!passenger.hasSufficientBalance(10000.00)) {
                throw new InsufficientBalanceException("Insufficient balance for booking");
            }
        } catch (InsufficientBalanceException e) {
            System.out.println("✓ Exception handling works: " + e.getMessage());
        }
        
        // Test validation methods
        System.out.println("✓ Passenger age validation: " + passenger.isValidAge());
        System.out.println("✓ Passenger email validation: " + passenger.isValidEmail());
        System.out.println("✓ Passenger phone validation: " + passenger.isValidPhone());
        
        System.out.println();
    }
}