package com.railway.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbSmoke {
    public static void main(String[] args) {
        try {
            DBConnectionManager mgr = DBConnectionManager.getInstance();
            try (Connection c = mgr.getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("PostgreSQL connection OK. SELECT 1 => " + rs.getInt(1));
                } else {
                    System.out.println("PostgreSQL connection executed but no result.");
                }
            }
        } catch (Exception e) {
            System.out.println("PostgreSQL connection FAILED: " + e.getMessage());
        }
    }
}




