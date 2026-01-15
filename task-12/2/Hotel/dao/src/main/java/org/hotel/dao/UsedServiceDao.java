package org.hotel.dao;

import org.hotel.annotations.Component;
import org.hotel.database.DBConnection;
import org.hotel.model.Guest;
import org.hotel.model.UsedService;
import org.hotel.model.UsedServiceRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class UsedServiceDao implements UsedServiceRepository {
    @Override
    public void save(UsedService usedService) {
        String sql = """
                INSERT INTO guestUsedServices (guestId, serviceId, price, date)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (usedServiceId) DO UPDATE SET
                    guestId = EXCLUDED.guestId,
                    serviceId = EXCLUDED.serviceId,
                    price = EXCLUDED.price,
                    date = EXCLUDED.date
                """;

        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, usedService.getGuestId());
            preparedStatement.setString(2, usedService.getServiceId());
            preparedStatement.setBigDecimal(3, usedService.getPrice());
            preparedStatement.setDate(4, new Date(usedService.getDate().getTime()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UsedService> findServicesUsedByGuest(Guest guest) {
        List<UsedService> usedServices = new ArrayList<>();
        String sql = """
                SELECT gus.*
                FROM guestUsedServices gus
                JOIN guests g ON g.guestId = gus.guestId
                WHERE rentRoomId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, guest.getRentRoomId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UsedService usedService = new UsedService();
                usedService.setServiceId(resultSet.getString("usedServiceId"));
                usedService.setGuestId(resultSet.getString("guestId"));
                usedService.setServiceId(resultSet.getString("serviceId"));
                usedService.setPrice(resultSet.getBigDecimal("price"));
                usedService.setDate(resultSet.getDate("date"));
                usedServices.add(usedService);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usedServices;
    }
}