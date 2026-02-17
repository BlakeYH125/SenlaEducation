package org.hotel.model.repository;

import org.hotel.model.entities.Room;
import org.hotel.model.enums.Status;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
public interface RoomRepository {
    void save(Room room);
    List<Room> findAll();
    void setAvailable(Room room);
    void setStatus(Room room, Date releasedIn, Status status);
    void setNewRoomPrice(Room room, BigDecimal price);
    Room getRoom(String id);
    List<Room> findFreeRoomsByDate(Date date);
}
