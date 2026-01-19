package org.hotel.dao;

import org.hotel.annotations.Component;
import org.hotel.database.DBConnection;
import org.hotel.model.Guest;
import org.hotel.model.GuestRepository;
import org.hotel.model.GuestStatus;
import org.hotel.model.Room;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public final class GuestDao implements GuestRepository {
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
    public void save(final Guest guestP) {
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
            preparedStatement.setString(FIFTH_PARAMETER, guestP.getId());
            preparedStatement.setString(SECOND_PARAMETER, guestP.getFullName());
            preparedStatement.setInt(THIRD_PARAMETER, guestP.getAge());
            preparedStatement.setString(FOURTH_PARAMETER, guestP.getRentRoomId());
            if (guestP.getArriveDate() != null) {
                preparedStatement.setDate(FIFTH_PARAMETER, new Date(guestP.getArriveDate().getTime()));
                preparedStatement.setDate(SIXTH_PARAMETER, new Date(guestP.getDepartureDate().getTime()));
            } else {
                preparedStatement.setDate(FIRST_PARAMETER, null);
                preparedStatement.setDate(SIXTH_PARAMETER, null);
            }
            preparedStatement.setString(SEVENTH_PARAMETER, guestP.getStatus().name());
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
    public List<Guest> findCurrentGuestsInRoom(final Room roomP) {
        List<Guest> guests = new ArrayList<>();
        String sql = """
                SELECT g.*
                FROM guests g
                JOIN rooms r ON r.roomId = g.rentRoomId
                WHERE r.roomId = ? AND g.status = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, roomP.getId());
            preparedStatement.setString(SECOND_PARAMETER, "SETTLED");
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
            preparedStatement.setString(FIRST_PARAMETER, "SETTLED");
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
    public Guest getGuest(final String idP) {
        String sql = """
                SELECT *
                FROM guests
                WHERE guestId = ?
                """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, idP);
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
    public List<Guest> findPreviousGuests(final Room roomP, final int limitP) {
        String sql = """
                SELECT *
                FROM GUESTS
                WHERE rentRoomId = ? AND status = ?
                limit ?
                """;
        List<Guest> guests = new ArrayList<>();
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, roomP.getId());
            preparedStatement.setString(SECOND_PARAMETER, "EVICTED");
            preparedStatement.setInt(THIRD_PARAMETER, limitP);
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
    public void setEvicted(final Guest guestP) {
        String sql = """
                    UPDATE guests
                    SET status = ?
                    WHERE guestId = ?;
                    """;
        try (PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement(sql)) {
            preparedStatement.setString(FIRST_PARAMETER, "EVICTED");
            preparedStatement.setString(SECOND_PARAMETER, guestP.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
