package dao;

import annotations.Component;
import database.DBConnection;
import model.Service;
import model.ServiceSection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class ServiceDao {
    public void save(Service service) {
        String sql = """
                INSERT INTO services (serviceId, name, price, serviceSection)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (serviceId) DO UPDATE SET
                    name = EXCLUDED.name,
                    price = EXCLUDED.price,
                    serviceSection = EXCLUDED.serviceSection
                """;

        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, service.getId());
            preparedStatement.setString(2, service.getName());
            preparedStatement.setBigDecimal(3, service.getPrice());
            preparedStatement.setString(4, service.getServiceSection().name());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Service> findAll() {
        List<Service> services = new ArrayList<>();
        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * from services")) {
            while (resultSet.next()) {
                Service service = new Service();
                service.setId(resultSet.getString("serviceId"));
                service.setName(resultSet.getString("name"));
                service.setPrice(resultSet.getBigDecimal("price"));
                service.setServiceSection(ServiceSection.valueOf(resultSet.getString("serviceSection").toUpperCase()));
                services.add(service);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public void setNewServicePrice(Service service, BigDecimal price) {
        String sql = "UPDATE services SET price = ? WHERE serviceId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, price);
            preparedStatement.setString(2, service.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Service getService(String id) {
        String sql = """
                SELECT *
                FROM services
                WHERE serviceId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Service service = new Service();
                service.setId(resultSet.getString("serviceId"));
                service.setName(resultSet.getString("name"));
                service.setPrice(resultSet.getBigDecimal("price"));
                service.setServiceSection(ServiceSection.valueOf(resultSet.getString("serviceSection").toUpperCase()));
                return service;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
