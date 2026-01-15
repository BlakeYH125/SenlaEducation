package org.hotel.dao;

import org.hotel.annotations.Component;
import org.hotel.database.DBConnection;
import org.hotel.model.Guest;
import org.hotel.model.GuestRepository;
import org.hotel.model.GuestStatus;
import org.hotel.model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class GuestDao implements GuestRepository {
    @Override
    public void save(Guest guest) {
        String sql = """
            INSERT INTO guests (guestId, fullName, age, rentRoomId, arriveDate, departureDate, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (guestId) DO UPDATE SET
                fullName = EXCLUDED.fullName,
                age = EXCLUDED.age,
                rentRoomId = EXCLUDED.rentRoomId,
                arriveDate = EXCLUDED.arriveDate,
                departureDate = EXCLUDED.departureDate,
                status = EXCLUDED.status
        """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, guest.getId());
            preparedStatement.setString(2, guest.getFullName());
            preparedStatement.setInt(3, guest.getAge());
            preparedStatement.setString(4, guest.getRentRoomId());
            if (guest.getArriveDate() != null) {
                preparedStatement.setDate(5, new Date(guest.getArriveDate().getTime()));
                preparedStatement.setDate(6, new Date(guest.getDepartureDate().getTime()));
            } else {
                preparedStatement.setDate(5, null);
                preparedStatement.setDate(6, null);
            }
            preparedStatement.setString(7, guest.getStatus().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Guest> findAll() {
        List<Guest> guests = new ArrayList<>();
        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * from guests")) {
            while (resultSet.next()) {
                Guest guest = new Guest();
                guest.setId(resultSet.getString("guestId"));
                guest.setFullName(resultSet.getString("fullName"));
                guest.setAge(resultSet.getInt("age"));
                guest.setRentRoomId(resultSet.getString("rentRoomId"));
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

    @Override
    public List<Guest> findCurrentGuestsInRoom(Room room) {
        List<Guest> guests = new ArrayList<>();
        String sql = """
                SELECT g.*
                FROM guests g
                JOIN rooms r ON r.roomId = g.rentRoomId
                WHERE r.roomId = ? AND g.status = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, room.getId());
            preparedStatement.setString(2, "SETTLED");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Guest guest = new Guest();
                guest.setId(resultSet.getString("guestId"));
                guest.setFullName(resultSet.getString("fullName"));
                guest.setAge(resultSet.getInt("age"));
                guest.setRentRoomId(resultSet.getString("rentRoomId"));
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

    @Override
    public List<Guest> findCurrentGuestsInHotel() {
        List<Guest> guests = new ArrayList<>();
        String sql = """
                SELECT *
                FROM guests
                WHERE status = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, "SETTLED");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Guest guest = new Guest();
                guest.setId(resultSet.getString("guestId"));
                guest.setFullName(resultSet.getString("fullName"));
                guest.setAge(resultSet.getInt("age"));
                guest.setRentRoomId(resultSet.getString("rentRoomId"));
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

    @Override
    public Guest getGuest(String id) {
        String sql = """
                SELECT *
                FROM guests
                WHERE guestId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Guest guest = new Guest();
                guest.setId(resultSet.getString("guestId"));
                guest.setFullName(resultSet.getString("fullName"));
                guest.setAge(resultSet.getInt("age"));
                guest.setRentRoomId(resultSet.getString("rentRoomId"));
                guest.setArriveDate(resultSet.getDate("arriveDate"));
                guest.setDepartureDate(resultSet.getDate("departureDate"));
                guest.setStatus(GuestStatus.valueOf(resultSet.getString("status").toUpperCase()));
                return guest;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Guest> findPreviousGuests(Room room, int limit) {
        String sql = """
                SELECT *
                FROM GUESTS
                WHERE rentRoomId = ? AND status = ?
                limit ?
                """;
        List<Guest> guests = new ArrayList<>();
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, room.getId());
            preparedStatement.setString(2, "EVICTED");
            preparedStatement.setInt(3, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Guest guest = new Guest();
                guest.setId(resultSet.getString("guestId"));
                guest.setFullName(resultSet.getString("fullName"));
                guest.setAge(resultSet.getInt("age"));
                guest.setRentRoomId(resultSet.getString("rentRoomId"));
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

    @Override
    public void setEvicted(Guest guest) {
        String sql = """
                    UPDATE guests
                    SET status = ?
                    WHERE guestId = ?;
                    """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, "EVICTED");
            preparedStatement.setString(2, guest.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
