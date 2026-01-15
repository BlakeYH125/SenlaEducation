package org.hotel.database;

import org.hotel.annotations.ConfigProperty;
import org.hotel.configurator.Configurator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;

    @ConfigProperty(propertyName = "url")
    private String url;

    @ConfigProperty(propertyName = "user")
    private String user;

    @ConfigProperty(propertyName = "password")
    private String password;

    private Connection connection;

    private DBConnection() {
        Configurator.configure(this);
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось подключиться к базе", e);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
