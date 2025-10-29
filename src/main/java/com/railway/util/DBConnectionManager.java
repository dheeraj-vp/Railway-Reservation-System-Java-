package com.railway.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Properties;

public class DBConnectionManager {
    private static final String PROPERTIES_FILE = "/database.properties";

    private static volatile DBConnectionManager instance;

    private final String url;
    private final String username;
    private final String password;
    private final String driver;

    private final int poolMaxActive;
    private final int poolMinIdle;
    private final ArrayDeque<Connection> pool = new ArrayDeque<>();

    private DBConnectionManager() {
        Properties props = new Properties();
        try (InputStream in = DBConnectionManager.class.getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                throw new IllegalStateException("database.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database.properties", e);
        }

        this.url = required(props, "db.url");
        this.username = required(props, "db.username");
        this.password = required(props, "db.password");
        this.driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        this.poolMaxActive = parseInt(props.getProperty("db.pool.maxActive", "10"));
        this.poolMinIdle = parseInt(props.getProperty("db.pool.minIdle", "2"));

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found: " + driver, e);
        }

        // Warm up a few idle connections (best-effort)
        for (int i = 0; i < poolMinIdle; i++) {
            try {
                pool.offer(createNewConnection());
            } catch (SQLException ignored) {
                // Best effort; continue
            }
        }
    }

    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) {
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        synchronized (pool) {
            Connection conn = pool.poll();
            if (conn != null && !conn.isClosed()) {
                return conn;
            }
            return createNewConnection();
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection == null) return;
        synchronized (pool) {
            if (pool.size() < poolMaxActive) {
                pool.offer(connection);
            } else {
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public void closeQuietly(Connection c) {
        if (c != null) {
            try { c.close(); } catch (SQLException ignored) {}
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    private static String required(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return v;
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}
