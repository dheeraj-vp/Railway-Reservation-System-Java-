package com.railway.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a passenger in the railway reservation system.
 */
public class Passenger implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private int age;
    private String email;
    private String phone;
    private double walletBalance;
    
    // Default constructor
    public Passenger() {
        this.id = generateId();
    }
    
    // Constructor with parameters (ID auto-generated)
    public Passenger(String name, int age, String email, String phone, double walletBalance) {
        this.id = generateId();
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.walletBalance = walletBalance;
    }
    
    // Constructor with all parameters including ID
    public Passenger(String id, String name, int age, String email, String phone, double walletBalance) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.walletBalance = walletBalance;
    }
    
    // Auto-generate passenger ID
    private String generateId() {
        return "P" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }
    
    // Utility methods
    public boolean hasSufficientBalance(double amount) {
        return walletBalance >= amount;
    }
    
    public void deductAmount(double amount) {
        if (amount > walletBalance) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.walletBalance -= amount;
    }
    
    public void addAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.walletBalance += amount;
    }
    
    public boolean isValidAge() {
        return age >= 1 && age <= 120;
    }
    
    public boolean isValidEmail() {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    public boolean isValidPhone() {
        return phone != null && phone.matches("\\d{10}");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return Objects.equals(id, passenger.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Passenger{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", walletBalance=" + walletBalance +
                '}';
    }
}