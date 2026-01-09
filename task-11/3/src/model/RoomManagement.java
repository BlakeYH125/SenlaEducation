package model;

import annotations.Component;
import annotations.ConfigProperty;
import annotations.Inject;
import annotations.PostConstruct;
import configurator.Configurator;
import dao.GuestDao;
import dao.RoomDao;
import dao.RoomGuestHistoryDao;

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

    @Inject
    RoomGuestHistoryDao roomGuestHistoryDao;

    private Map<String, Room> rooms;

    @ConfigProperty(propertyName = "hotel.room.status.changing")
    private boolean isAllowChange;

    @ConfigProperty(propertyName = "hotel.room.history.limit")
    private int previousGuestsLimit;

    public RoomManagement() {
        this.rooms = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        Configurator.configure(this);
        reload();
    }

    public void reload() {
        rooms.clear();
        List<Room> rooms = roomDao.findAll();
        for (Room room : rooms) {
            this.rooms.put(room.getId(), room);
        }
    }

    public Map<String, Room> getRooms() {
        return new HashMap<>(rooms);
    }

    public void addNewRoom(Room room) {
        roomDao.save(room);
        rooms.put(room.getId(), room);
    }

    public boolean setAvailable(String id) {
        if (isAllowChange) {
            roomDao.setAvailable(getRoom(id));
            Room room = rooms.get(id);
            room.setReleasedIn(null);
            room.setStatus(Status.AVAILABLE);
            return true;
        }
        return false;
    }

    public void setAvailableToEvict(String id) {
        roomDao.setAvailable(getRoom(id));
        Room room = rooms.get(id);
        room.setReleasedIn(null);
        room.setStatus(Status.AVAILABLE);
    }

    public boolean setOccupied(String id, int daysCount) {
        if (isAllowChange) {
            roomDao.setOccupied(getRoom(id));
            Room room = rooms.get(id);
            room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
            room.setStatus(Status.OCCUPIED);
            return true;
        }
        return false;
    }

    public void setOccupiedToSettle(String id, int daysCount) {
        roomDao.setOccupied(getRoom(id));
        Room room = rooms.get(id);
        room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
        room.setStatus(Status.OCCUPIED);
    }

    public boolean setInService(String id, int daysCount) {
        if (isAllowChange) {
            roomDao.setInService(getRoom(id));
            Room room = rooms.get(id);
            room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
            room.setStatus(Status.IN_SERVICE);
            return true;
        }
        return false;
    }

    public boolean isFree(String id) {
        return rooms.get(id).getStatus() == Status.AVAILABLE;
    }

    public boolean isServicing(String id) {
        return rooms.get(id).getStatus() == Status.IN_SERVICE;
    }

    public boolean isOccupied(String id) {
        return rooms.get(id).getStatus() == Status.OCCUPIED;
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public static long getMSecInDay() {
        return MSEC_IN_DAY;
    }

    public void setNewRoomPrice(String id, BigDecimal newPrice) {
        roomDao.setNewRoomPrice(getRoom(id), newPrice);
        Room room = rooms.get(id);
        room.setPrice(newPrice);
    }

    public List<Guest> getThreePrevRoomGuests(String id) {
        return roomGuestHistoryDao.findPreviousGuests(getRoom(id), Math.min(3, previousGuestsLimit));
    }

    public BigDecimal getTotalRoomCost(String id) {
        Room room = rooms.get(id);
        List<Guest> guests = guestDao.findCurrentGuests(getRoom(id));
        long millis = guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime();
        BigDecimal days = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(MSEC_IN_DAY), 2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = room.getPrice().multiply(days);
        return totalPrice;}

    public int getFreeRoomsCount() {
        int count = 0;
        for (Room room : rooms.values()) {
            if (room.getStatus() == Status.AVAILABLE) {
                count++;
            }
        }
        return count;
    }

    public List<Room> getFreeRoomsByDate(Date date) {
        List<Room> filteredRoom = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (room.getReleasedIn() == null || room.getReleasedIn().before(date)) {
                filteredRoom.add(room);
            }
        }
        return filteredRoom;
    }

    public String getRoomDetails(String id) {
        Room room = rooms.get(id);
        return room.toString();
    }

    public List<Room> getAllRoomsWithSort(SortType sortType) {
        List<Room> listRooms = new ArrayList<>(rooms.values());
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

    public void addToPrevGuests(Room room, List<Guest> guests) {
        for (Guest guest : guests) {
            roomGuestHistoryDao.save(room, guest);
        }
    }

    public List<Guest> getCurrentGuests(Room room) {
        return guestDao.findCurrentGuests(room);
    }
}
