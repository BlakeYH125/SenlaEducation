package org.hotel.dao;

import org.hotel.annotations.Component;
import org.hotel.database.DBConnection;
import org.hotel.model.Service;
import org.hotel.model.ServiceRepository;
import org.hotel.model.ServiceSection;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public final class ServiceDao implements ServiceRepository {
    /**
     * Первый параметр.
     */
    private static final int FIRST_PARAMETER = 1;

    /**
     * Второй параметр.
     */
    private static final int SECOND_PARAMETER = 2;

    /**
     * Третий параметр.
     */
    private static final int THIRD_PARAMETER = 3;

    /**
     * Четвертый параметр.
     */
    private static final int FOURTH_PARAMETER = 4;

    @Override
    public void save(final Service serviceP) {
        String sql = """
                INSERT INTO services (serviceId, name, price, serviceSection)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (serviceId) DO UPDATE SET
                    name = EXCLUDED.name,
                    price = EXCLUDED.price,
                    serviceSection = EXCLUDED.serviceSection
                """;

        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, serviceP.getId());
            preparedStatement.setString(SECOND_PARAMETER, serviceP.getName());
            preparedStatement.setBigDecimal(THIRD_PARAMETER, serviceP.getPrice());
            preparedStatement.setString(FOURTH_PARAMETER, serviceP.getServiceSection().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public void setNewServicePrice(final Service serviceP, final BigDecimal priceP) {
        String sql = "UPDATE services SET price = ? WHERE serviceId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setBigDecimal(FIRST_PARAMETER, priceP);
            preparedStatement.setString(SECOND_PARAMETER, serviceP.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Service getService(final String idP) {
        String sql = """
                SELECT *
                FROM services
                WHERE serviceId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, idP);
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
