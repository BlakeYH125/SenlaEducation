package org.hotel.database;

import org.hotel.annotations.ConfigProperty;
import org.hotel.configurator.Configurator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    /**
     * Единственный экземпляр класса.
     */
    private static DBConnection instance;

    /**
     * Ссылка на базу данных.
     */
    @ConfigProperty(propertyName = "url")
    private String url;

    /**
     * Ссылка на имя пользователя.
     */
    @ConfigProperty(propertyName = "user")
    private String user;

    /**
     * Ссылка на пароль.
     */
    @ConfigProperty(propertyName = "password")
    private String password;

    /**
     * Ссылка на подключение к базе данных.
     */
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
