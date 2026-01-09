package dao;

import annotations.Component;
import database.DBConnection;
import model.Guest;
import model.GuestStatus;
import model.Room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoomGuestHistoryDao {
    public void save(Room room, Guest guest) {
        String sql = """
                INSERT INTO roomGuestHistory (roomId, guestId, arriveDate, departureDate)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, room.getId());
            preparedStatement.setString(2, guest.getId());
            preparedStatement.setDate(3, new java.sql.Date(guest.getArriveDate().getTime()));
            preparedStatement.setDate(4, new java.sql.Date(guest.getDepartureDate().getTime()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Guest> findPreviousGuests(Room room, int limit) {
        String sql = """
                SELECT g.guestId, g.fullName, g.age, g.status, h.arriveDate, h.departureDate
                FROM roomGuestHistory h
                JOIN guests g ON g.guestId = h.guestId
                WHERE h.roomId = ?
                ORDER BY h.departureDate DESC
                LIMIT ?
                """;
        List<Guest> guests = new ArrayList<>();
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, room.getId());
            preparedStatement.setInt(2, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Guest guest = new Guest();
                guest.setId(resultSet.getString("guestId"));
                guest.setFullName(resultSet.getString("fullName"));
                guest.setAge(resultSet.getInt("age"));
                guest.setArriveDate(resultSet.getDate("arriveDate"));
                guest.setDepartureDate(resultSet.getDate("departureDate"));
                guest.setStatus(GuestStatus.valueOf(resultSet.getString("status").toUpperCase()));
                guests.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }
}
