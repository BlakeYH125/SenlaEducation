import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class RoomManagement {
    private static final long MSEC_IN_DAY = 86400000;

    private Map<Integer, Room> rooms;

    public RoomManagement() {
        this.rooms = new HashMap<>();
    }

    public Map<Integer, Room> getRooms() {
        return rooms;
    }

    public void addNewRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }


    public void setAvailable(int number) {
        Room room = rooms.get(number);
        room.setReleasedIn(null);
        room.setStatus(Status.AVAILABLE);
    }

    public void setOccupied(int number, int daysCount) {
        Room room = rooms.get(number);
        room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
        room.setStatus(Status.OCCUPIED);
    }

    public void setInService(int number, int daysCount) {
        Room room = rooms.get(number);
        room.setReleasedIn(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
        room.setStatus(Status.IN_SERVICE);
    }

    public boolean isFree(int number) {
        return rooms.get(number).getStatus() == Status.AVAILABLE;
    }

    public boolean isServicing(int number) {
        return rooms.get(number).getStatus() == Status.IN_SERVICE;
    }

    public boolean isOccupied(int number) {
        return rooms.get(number).getStatus() == Status.OCCUPIED;
    }

    public Room getRoom(int number) {
        return rooms.get(number);
    }

    public static long getMSecInDay() {
        return MSEC_IN_DAY;
    }

    public void setNewRoomPrice(int number, double newPrice) {
        Room room = rooms.get(number);
        room.setPrice(newPrice);
    }

    public List<Guest> getThreePrevRoomGuests(int number) {
        Room room = rooms.get(number);
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

    public double getTotalRoomCost(int number) {
        Room room = rooms.get(number);
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

    public String getRoomDetails(int number) {
        Room room = rooms.get(number);
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
