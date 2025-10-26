import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Administrator {
    private Map<Integer, Room> rooms;
    private Map<String, Service> services;
    private List<Guest> guests;

    public Administrator() {
        rooms = new HashMap<>();
        services = new HashMap<>();
        guests = new ArrayList<>();
    }

    public Map<Integer, Room> getRooms() {
        return rooms;
    }

    public Map<String, Service> getServices() {
        return services;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void addRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }

    public void addGuest(Guest guest) {
        guests.add(guest);
    }

    public void addNewService(Service service) {
        services.put(service.getName(), service);
    }


    public void settle(int number, Guest guest) {
        Room room = rooms.get(number);
        if (room.getStatus() == Status.OCCUPIED) {
            System.out.println("В данный момент комната занята.");
        } else if (room.getStatus() == Status.IN_SERVICE) {
            System.out.println("В данный момент комната на обслуживании.");
        } else {
            room.setGuest(guest);
            setStatus(room.getNumber(), Status.OCCUPIED);
            guest.setRentRoom(room);
            rooms.put(room.getNumber(), room);
        }
    }

    public void evict(int number) {
        Room room = rooms.get(number);
        Guest guest = room.getGuest();
        if (room.getStatus() == Status.AVAILABLE) {
            System.out.println("В данный момент комната свободна.");
        } else {
            guest.setRentRoom(null);
            room.setGuest(null);
            setStatus(room.getNumber(), Status.AVAILABLE);
            rooms.put(room.getNumber(), room);
        }
    }

    public void setStatus(int number, Status status) {
        Room room = rooms.get(number);
        room.setStatus(status);
    }

    public void setNewRoomPrice(int number, double newPrice) {
        Room room = rooms.get(number);
        room.setPrice(newPrice);
    }

    public void setNewServicePrice(String name, double newPrice) {
        Service service = services.get(name);
        service.setPrice(newPrice);
    }

    public void addNewRoom(Room room) {
        rooms.put(room.getNumber(), room);
    }
}
