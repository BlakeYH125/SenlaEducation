package model;

import annotations.Component;
import annotations.ConfigProperty;
import annotations.Inject;
import configurator.Configurator;
import dao.GuestDao;
import dao.RoomDao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class RoomManagement {
    private static final long MSEC_IN_DAY = 86400000;

    @Inject
    RoomDao roomDao;

    @Inject
    GuestDao guestDao;

    @ConfigProperty(propertyName = "hotel.room.status.changing")
    private boolean isAllowChange;

    @ConfigProperty(propertyName = "hotel.room.history.limit")
    private int previousGuestsLimit;

    public RoomManagement() {
        Configurator.configure(this);
    }

    public List<Room> getRooms() {
        return roomDao.findAll();
    }

    public boolean isThereRoom(String id) {
        if (roomDao.getRoom(id) == null) {
            return false;
        }
        return true;
    }

    public Room getRoom(String id) {
        return roomDao.getRoom(id);
    }

    public void addNewRoom(Room room) {
        roomDao.save(room);
    }

    public boolean setAvailable(String id) {
        if (isAllowChange) {
            roomDao.setAvailable(getRoom(id));
            return true;
        }
        return false;
    }

    public void setAvailableToEvict(String id) {
        roomDao.setAvailable(getRoom(id));
    }

    public boolean setStatus(String id, int daysCount, Status status) {
        if (isAllowChange) {
            roomDao.setStatus(getRoom(id), new java.sql.Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY), status);
            return true;
        }
        return false;
    }

    public void setOccupiedToSettle(String id, int daysCount) {
        roomDao.setStatus(getRoom(id), new java.sql.Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY), Status.OCCUPIED);
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
        roomDao.setNewRoomPrice(getRoom(id), newPrice);
    }

    public List<Guest> getThreePrevRoomGuests(String id) {
        return guestDao.findPreviousGuests(getRoom(id), Math.min(3, previousGuestsLimit));
    }

    public BigDecimal getTotalRoomCost(String id) {
        Room room = getRoom(id);
        List<Guest> guests = guestDao.findCurrentGuestsInRoom(getRoom(id));
        long millis = guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime();
        BigDecimal days = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(MSEC_IN_DAY), 2, RoundingMode.HALF_UP);
        return room.getPrice().multiply(days);
    }

    public int getFreeRoomsCount() {
        return new ArrayList<>(getFreeRoomsByDate(new Date(System.currentTimeMillis()))).size();
    }

    public List<Room> getFreeRoomsByDate(Date date) {
        return roomDao.findFreeRoomsByDate(new java.sql.Date(date.getTime()));
    }

    public String getRoomDetails(String id) {
        Room room = roomDao.getRoom(id);
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
        return guestDao.findCurrentGuestsInRoom(room);
    }
}
