package org.hotel.model;

import org.hotel.annotations.Component;
import org.hotel.annotations.ConfigProperty;
import org.hotel.annotations.Inject;
import org.hotel.configurator.Configurator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class RoomManagement {
    private static final long MSEC_IN_DAY = 86400000;

    @Inject
    RoomRepository roomRepository;

    @Inject
    GuestRepository guestRepository;

    @ConfigProperty(propertyName = "hotel.room.status.changing")
    private boolean isAllowChange;

    @ConfigProperty(propertyName = "hotel.room.history.limit")
    private int previousGuestsLimit;

    public RoomManagement() {
        Configurator.configure(this);
    }

    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    public boolean isThereRoom(String id) {
        if (roomRepository.getRoom(id) == null) {
            return false;
        }
        return true;
    }

    public Room getRoom(String id) {
        return roomRepository.getRoom(id);
    }

    public void addNewRoom(Room room) {
        roomRepository.save(room);
    }

    public boolean setAvailable(String id) {
        if (isAllowChange) {
            roomRepository.setAvailable(getRoom(id));
            return true;
        }
        return false;
    }

    public void setAvailableToEvict(String id) {
        roomRepository.setAvailable(getRoom(id));
    }

    public boolean setStatus(String id, int daysCount, Status status) {
        if (isAllowChange) {
            roomRepository.setStatus(getRoom(id), new java.sql.Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY), status);
            return true;
        }
        return false;
    }

    public void setOccupiedToSettle(String id, int daysCount) {
        roomRepository.setStatus(getRoom(id), new java.sql.Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY), Status.OCCUPIED);
    }

    public boolean isFree(String id) {
        return getRoom(id).getStatus() == Status.AVAILABLE;
    }

    public boolean isServicing(String id) {
        return getRoom(id).getStatus() == Status.IN_SERVICE;
    }

    public boolean isOccupied(String id) {
        return getRoom(id).getStatus() == Status.OCCUPIED;
    }

    public static long getMSecInDay() {
        return MSEC_IN_DAY;
    }

    public void setNewRoomPrice(String id, BigDecimal newPrice) {
        roomRepository.setNewRoomPrice(getRoom(id), newPrice);
    }

    public List<Guest> getThreePrevRoomGuests(String id) {
        return guestRepository.findPreviousGuests(getRoom(id), Math.min(3, previousGuestsLimit));
    }

    public BigDecimal getTotalRoomCost(String id) {
        Room room = getRoom(id);
        List<Guest> guests = guestRepository.findCurrentGuestsInRoom(getRoom(id));
        long millis = guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime();
        BigDecimal days = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(MSEC_IN_DAY), 2, RoundingMode.HALF_UP);
        return room.getPrice().multiply(days);
    }

    public int getFreeRoomsCount() {
        return new ArrayList<>(getFreeRoomsByDate(new Date(System.currentTimeMillis()))).size();
    }

    public List<Room> getFreeRoomsByDate(Date date) {
        return roomRepository.findFreeRoomsByDate(new java.sql.Date(date.getTime()));
    }

    public String getRoomDetails(String id) {
        Room room = roomRepository.getRoom(id);
        return room.toString();
    }

    public List<Room> getAllRoomsWithSort(SortType sortType) {
        List<Room> listRooms = new ArrayList<>(getRooms());
        if (sortType == SortType.PRICE) {
            listRooms.sort(Comparator.comparing(Room::getPrice));
        } else if (sortType == SortType.CAPACITY) {
            listRooms.sort(Comparator.comparing(Room::getCapacity));
        } else if (sortType == SortType.STARS) {
            listRooms.sort(Comparator.comparing(Room::getStars));
        }
        return listRooms;
    }

    public List<Room> getFreeRoomsWithSort(SortType sortType) {
        List<Room> listRooms = new ArrayList<>(getFreeRoomsByDate(new Date(System.currentTimeMillis())));
        if (sortType == SortType.PRICE) {
            listRooms.sort(Comparator.comparing(Room::getPrice));
        } else if (sortType == SortType.CAPACITY) {
            listRooms.sort(Comparator.comparing(Room::getCapacity));
        } else if (sortType == SortType.STARS) {
            listRooms.sort(Comparator.comparing(Room::getStars));
        }
        return listRooms;
    }

    public List<Guest> getCurrentGuests(Room room) {
        return guestRepository.findCurrentGuestsInRoom(room);
    }
}
