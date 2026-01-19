package org.hotel.dao;

import org.hotel.annotations.Component;
import org.hotel.database.DBConnection;
import org.hotel.model.Room;
import org.hotel.model.RoomRepository;
import org.hotel.model.Status;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public final class RoomDao implements RoomRepository {
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

    /**
     * Пятый параметр.
     */
    private static final int FIFTH_PARAMETER = 5;

    /**
     * Шестой параметр.
     */
    private static final int SIXTH_PARAMETER = 6;

    /**
     * Седьмой параметр.
     */
    private static final int SEVENTH_PARAMETER = 7;

    @Override
    public void save(final Room roomP) {
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
            preparedStatement.setString(FIRST_PARAMETER, roomP.getId());
            preparedStatement.setInt(SECOND_PARAMETER, roomP.getNumber());
            preparedStatement.setBigDecimal(THIRD_PARAMETER, roomP.getPrice());
            preparedStatement.setInt(FOURTH_PARAMETER, roomP.getCapacity());
            preparedStatement.setInt(FIFTH_PARAMETER, roomP.getStars());
            preparedStatement.setString(SIXTH_PARAMETER, roomP.getStatus().name());
            if (roomP.getReleasedIn() != null) {
                preparedStatement.setDate(SEVENTH_PARAMETER, new Date(roomP.getReleasedIn().getTime()));
            } else {
                preparedStatement.setDate(SEVENTH_PARAMETER, null);
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public void setAvailable(final Room roomP) {
        String sql = "UPDATE rooms SET status = ?, releasedIn = ? WHERE roomId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, "AVAILABLE");
            preparedStatement.setDate(SECOND_PARAMETER, null);
            preparedStatement.setString(THIRD_PARAMETER, roomP.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setStatus(final Room roomP, final Date releasedInP, final Status statusP) {
        String sql = "UPDATE rooms SET status = ?, releasedIn = ? WHERE roomId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, statusP.name());
            preparedStatement.setDate(SECOND_PARAMETER, new Date(releasedInP.getTime()));
            preparedStatement.setString(THIRD_PARAMETER, roomP.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setNewRoomPrice(final Room roomP, final BigDecimal priceP) {
        String sql = "UPDATE rooms SET price = ? WHERE roomId = ?";
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setBigDecimal(FIRST_PARAMETER, priceP);
            preparedStatement.setString(SECOND_PARAMETER, roomP.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Room getRoom(final String idP) {
        String sql = """
                SELECT *
                FROM rooms
                WHERE roomId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, idP);
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

    @Override
    public List<Room> findFreeRoomsByDate(final Date dateP) {
        List<Room> rooms = new ArrayList<>();
        String sql = """
                SELECT *
                FROM rooms
                WHERE releasedIn < ? OR status = 'AVAILABLE'
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setDate(FIRST_PARAMETER, new Date(dateP.getTime()));
            ResultSet resultSet = preparedStatement.executeQuery();
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
}
