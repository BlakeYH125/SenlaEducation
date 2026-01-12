package dao;

import annotations.Component;
import database.DBConnection;
import model.GuestStatus;
import model.Room;
import model.Status;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoomDao {
    public void save(Room room) {
        String sql = """
                INSERT INTO rooms (roomId, number, price, capacity, stars, status, releasedIn)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (roomId) DO UPDATE SET
                    number = EXCLUDED.number,
                    price = EXCLUDED.price,
                    capacity = EXCLUDED.capacity,
                    stars = EXCLUDED.stars,
                    status = EXCLUDED.status,
                    releasedIn = EXCLUDED.releasedIn
                """;

        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, room.getId());
            preparedStatement.setInt(2, room.getNumber());
            preparedStatement.setBigDecimal(3, room.getPrice());
            preparedStatement.setInt(4, room.getCapacity());
            preparedStatement.setInt(5, room.getStars());
            preparedStatement.setString(6, room.getStatus().name());
            if (room.getReleasedIn() != null) {
                preparedStatement.setDate(7, new java.sql.Date(room.getReleasedIn().getTime()));
            } else {
                preparedStatement.setDate(7, null);
            }
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        try (Statement statement = DBConnection.getInstance().getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * from rooms")) {
            while (resultSet.next()) {
                Room room = new Room();
                room.setId(resultSet.getString("roomId"));
                room.setNumber(resultSet.getInt("number"));
                room.setPrice(resultSet.getBigDecimal("price"));
                room.setCapacity(resultSet.getInt("capacity"));
                room.setStars(resultSet.getInt("stars"));
                room.setStatus(Status.valueOf(resultSet.getString("status")));
                room.setReleasedIn(resultSet.getDate("releasedIn"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public void setAvailable(Room room) {
        String sql = "UPDATE rooms SET status = ?, releasedIn = ? WHERE roomId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, "AVAILABLE");
            preparedStatement.setDate(2, null);
            preparedStatement.setString(3, room.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setStatus(Room room, Date releasedIn, Status status) {
        String sql = "UPDATE rooms SET status = ?, releasedIn = ? WHERE roomId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, status.name());
            preparedStatement.setDate(2, new java.sql.Date(releasedIn.getTime()));
            preparedStatement.setString(3, room.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setNewRoomPrice(Room room, BigDecimal price) {
        String sql = "UPDATE rooms SET price = ? WHERE roomId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setBigDecimal(1, price);
            preparedStatement.setString(2, room.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Room getRoom(String id) {
        String sql = """
                SELECT *
                FROM rooms
                WHERE roomId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Room room = new Room();
                room.setId(resultSet.getString("roomId"));
                room.setNumber(resultSet.getInt("number"));
                room.setPrice(resultSet.getBigDecimal("price"));
                room.setCapacity(resultSet.getInt("capacity"));
                room.setStars(resultSet.getInt("stars"));
                room.setStatus(Status.valueOf(resultSet.getString("status")));
                room.setReleasedIn(resultSet.getDate("releasedIn"));
                return room;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
