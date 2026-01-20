package org.hotel.dao;

import org.hotel.annotations.Component;
import org.hotel.database.DBConnection;
import org.hotel.model.Guest;
import org.hotel.model.UsedService;
import org.hotel.model.UsedServiceRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public final class UsedServiceDao implements UsedServiceRepository {
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
    public void save(final UsedService usedServiceP) {
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
            preparedStatement.setString(FIRST_PARAMETER, usedServiceP.getGuestId());
            preparedStatement.setString(SECOND_PARAMETER, usedServiceP.getServiceId());
            preparedStatement.setBigDecimal(THIRD_PARAMETER, usedServiceP.getPrice());
            preparedStatement.setDate(FOURTH_PARAMETER, new Date(usedServiceP.getDate().getTime()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UsedService> findServicesUsedByGuest(final Guest guestP) {
        List<UsedService> usedServices = new ArrayList<>();
        String sql = """
                SELECT gus.*
                FROM guestUsedServices gus
                JOIN guests g ON g.guestId = gus.guestId
                WHERE rentRoomId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, guestP.getRentRoomId());
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
