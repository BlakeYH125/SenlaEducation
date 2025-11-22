package model;

import java.io.Serializable;
import java.util.*;

public class RoomManagement implements Serializable {
    private static final long MSEC_IN_DAY = 86400000;

    private Map<String, Room> rooms;
    private transient Properties settings;

    public RoomManagement(Properties settings) {
        this.rooms = new HashMap<>();
        this.settings = settings;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public void addNewRoom(Room room) {
        rooms.put(room.getId(), room);
    }

    public boolean setAvailable(String id) {
        String allowChange = settings.getProperty("hotel.room.status.changing");
        boolean isAllowChange = Boolean.parseBoolean(allowChange);
        if (isAllowChange) {
            Room room = rooms.get(id);
            room.setReleasedIn(null);
            room.setStatus(Status.AVAILABLE);
            return true;
        }
        return false;
    }

    public void setAvailableToEvict(String id) {
        Room room = rooms.get(id);
        room.setReleasedIn(null);
        room.setStatus(Status.AVAILABLE);
    }

    public boolean setOccupied(String id, int daysCount) {
        String allowChange = settings.getProperty("hotel.room.status.changing");
        boolean isAllowChange = Boolean.parseBoolean(allowChange);
        if (isAllowChange) {
            Room room = rooms.get(id);
            room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
            room.setStatus(Status.OCCUPIED);
            return true;
        }
        return false;
    }

    public void setOccupiedToSettle(String id, int daysCount) {
        Room room = rooms.get(id);
        room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
        room.setStatus(Status.OCCUPIED);
    }

    public boolean setInService(String id, int daysCount) {
        String allowChange = settings.getProperty("hotel.room.status.changing");
        boolean isAllowChange = Boolean.parseBoolean(allowChange);
        if (isAllowChange) {
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

    public void setNewRoomPrice(String id, double newPrice) {
        Room room = rooms.get(id);
        room.setPrice(newPrice);
    }

    public List<Guest> getThreePrevRoomGuests(String id) {
        Room room = rooms.get(id);
        List<Guest> prevGuests = room.getPrevGuests();

        List<Guest> threePrevGuests;

        if (prevGuests.size() < 3) {
            threePrevGuests = new ArrayList<>(prevGuests);
        } else {
            threePrevGuests = new ArrayList<>();
            threePrevGuests.add(prevGuests.get(prevGuests.size() - 1));
            threePrevGuests.add(prevGuests.get(prevGuests.size() - 2));
            threePrevGuests.add(prevGuests.get(prevGuests.size() - 3));
        }
        return threePrevGuests;
    }

    public double getTotalRoomCost(String id) {
        Room room = rooms.get(id);
        List<Guest> guests = room.getGuests();
        return (double) (guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime()) / MSEC_IN_DAY * room.getPrice();
    }

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
}
