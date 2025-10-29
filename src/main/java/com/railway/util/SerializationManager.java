package com.railway.util;

import com.railway.model.Booking;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializationManager {

    public static void serializeBooking(Booking booking, String filepath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)))) {
            oos.writeObject(booking);
        }
    }

    public static Booking deserializeBooking(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
            return (Booking) ois.readObject();
        }
    }

    public static void serializeBookingList(List<Booking> bookings, String filepath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filepath)))) {
            oos.writeObject(new ArrayList<>(bookings));
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Booking> deserializeBookingList(String filepath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filepath)))) {
            return (List<Booking>) ois.readObject();
        }
    }
}




