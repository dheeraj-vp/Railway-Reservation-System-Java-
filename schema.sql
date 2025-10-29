-- Railway Reservation System Database Schema
-- Essential tables with sample data

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS passengers;
DROP TABLE IF EXISTS trains;

-- Create trains table
CREATE TABLE trains (
    train_id VARCHAR(20) PRIMARY KEY,
    train_name VARCHAR(100) NOT NULL,
    source VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    fare DECIMAL(10,2) NOT NULL,
    departure_time TIMESTAMP NOT NULL
);

-- Create passengers table
CREATE TABLE passengers (
    passenger_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    wallet_balance DECIMAL(10,2) DEFAULT 0.00
);

-- Create bookings table
CREATE TABLE bookings (
    booking_id VARCHAR(20) PRIMARY KEY,
    passenger_id VARCHAR(20) NOT NULL,
    train_id VARCHAR(20) NOT NULL,
    number_of_tickets INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id) ON DELETE CASCADE,
    FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE CASCADE
);

-- Insert sample train data
INSERT INTO trains VALUES 
('T001', 'Rajdhani Express', 'Delhi', 'Mumbai', 100, 100, 1500.00, '2025-11-01 08:00:00'),
('T002', 'Shatabdi Express', 'Chennai', 'Bangalore', 80, 80, 800.00, '2025-11-01 10:00:00'),
('T003', 'Duronto Express', 'Kolkata', 'Delhi', 120, 120, 1200.00, '2025-11-01 09:30:00'),
('T004', 'Garib Rath', 'Mumbai', 'Goa', 150, 150, 500.00, '2025-11-01 11:15:00'),
('T005', 'Vande Bharat', 'Delhi', 'Varanasi', 90, 90, 2000.00, '2025-11-01 12:30:00');

-- Insert sample passenger data
INSERT INTO passengers VALUES 
('P12345678', 'John Doe', 35, 'john.doe@email.com', '9876543210', 5000.00),
('P87654321', 'Jane Smith', 28, 'jane.smith@email.com', '9876543211', 3000.00),
('P11223344', 'Raj Kumar', 42, 'raj.kumar@email.com', '9876543212', 7500.00),
('P55667788', 'Priya Sharma', 25, 'priya.sharma@email.com', '9876543213', 2000.00),
('P99887766', 'Amit Patel', 38, 'amit.patel@email.com', '9876543214', 4000.00);

-- Insert sample booking data
INSERT INTO bookings VALUES 
('B12345678', 'P12345678', 'T001', 2, 3000.00, '2025-10-29 10:30:00', 'CONFIRMED'),
('B87654321', 'P87654321', 'T002', 1, 800.00, '2025-10-29 11:15:00', 'CONFIRMED'),
('B11223344', 'P11223344', 'T003', 3, 3600.00, '2025-10-29 12:00:00', 'CONFIRMED'),
('B55667788', 'P55667788', 'T004', 1, 500.00, '2025-10-29 13:45:00', 'CONFIRMED'),
('B99887766', 'P99887766', 'T005', 2, 4000.00, '2025-10-29 14:20:00', 'CONFIRMED');

-- Update available seats based on bookings
UPDATE trains SET available_seats = available_seats - 2 WHERE train_id = 'T001';
UPDATE trains SET available_seats = available_seats - 1 WHERE train_id = 'T002';
UPDATE trains SET available_seats = available_seats - 3 WHERE train_id = 'T003';
UPDATE trains SET available_seats = available_seats - 1 WHERE train_id = 'T004';
UPDATE trains SET available_seats = available_seats - 2 WHERE train_id = 'T005';

-- Create indexes for better performance
CREATE INDEX idx_trains_source_destination ON trains(source, destination);
CREATE INDEX idx_trains_departure_time ON trains(departure_time);
CREATE INDEX idx_passengers_email ON passengers(email);
CREATE INDEX idx_bookings_passenger_id ON bookings(passenger_id);
CREATE INDEX idx_bookings_train_id ON bookings(train_id);
CREATE INDEX idx_bookings_booking_date ON bookings(booking_date);