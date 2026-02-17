package org.hotel.model.repository;

import org.hotel.model.entities.Guest;
import org.hotel.model.entities.Room;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GuestRepository {
    void save(Guest guest);
    List<Guest> findAll();
    Guest getGuest(String id);
    void setEvicted(Guest guest);
    List<Guest> findCurrentGuestsInHotel();
    List<Guest> findCurrentGuestsInRoom(Room room);
    List<Guest> findPreviousGuests(Room room, int limit);
}
