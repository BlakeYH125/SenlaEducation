package org.hotel.model;

import org.hotel.annotations.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Component
public interface RoomRepository {
    void save(Room room);
    List<Room> findAll();
    void setAvailable(Room room);
    void setStatus(Room room, Date releasedIn, Status status);
    void setNewRoomPrice(Room room, BigDecimal price);
    Room getRoom(String id);
    List<Room> findFreeRoomsByDate(Date date);
}
