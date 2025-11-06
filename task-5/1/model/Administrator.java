import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Administrator {
    private static final long MSEC_IN_DAY = 86400000;
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

    public List<Service> getServices() {
        List<Service> temp = new ArrayList<>(services.values());
        return temp;
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

    public void removeGuest(Guest guest) {
        guests.remove(guest);
    }

    public void addNewService(Service service) {
        services.put(service.getName().toLowerCase(), service);
    }


    public int settle(int number, List<Guest> guests, int daysCount) {
        Room room = rooms.get(number);
        if (room.getStatus() == Status.OCCUPIED) {
            return -2;
        } else if (room.getStatus() == Status.IN_SERVICE) {
            return -1;
        } else {
            room.setGuests(guests);
            setOccupied(number, daysCount);
            for (Guest guest : guests) {
                guest.setRentRoom(room);
                guest.setArriveDate(new Date(System.currentTimeMillis()));
                guest.setDepartureDate(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
                addGuest(guest);
            }
            rooms.put(room.getNumber(), room);
            return 0;
        }
    }

    public boolean evict(int number) {
        Room room = rooms.get(number);
        List<Guest> guests = room.getGuests();
        if (room.getStatus() == Status.AVAILABLE || room.getStatus() == Status.IN_SERVICE) {
            return false;
        } else {
            room.addToPrevGuestsList();
            for (Guest guest : guests) {
                guest.setRentRoom(null);
                guest.setDepartureDate(new Date(System.currentTimeMillis()));
                removeGuest(guest);
            }
            setAvailable(number);
            rooms.put(room.getNumber(), room);
            return true;
        }
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

    public void setNewRoomPrice(int number, double newPrice) {
        Room room = rooms.get(number);
        room.setPrice(newPrice);
    }

    public void setNewServicePrice(String name, double newPrice) {
        name = name.toLowerCase();
        Service service = services.get(name);
        service.setPrice(newPrice);
    }

    public void addNewRoom(Room room) {
        rooms.put(room.getNumber(), room);
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

    public int getGuestsCount() {
        return guests.size();
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

    public List<Guest> getThreePrevGuests(int number) {
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

    public double getTotalCost(int number) {
        Room room = rooms.get(number);
        List<Guest> guests = room.getGuests();
        return (guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime()) / MSEC_IN_DAY * room.getPrice();
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

    public List<Guest> getGuestsWithSort(SortType sortType) {
        List<Guest> listGuests = new ArrayList<>(guests);
        if (sortType == SortType.ALPHABET) {
            listGuests.sort(Comparator.comparing(Guest::getFullName));
        } else if (sortType == SortType.DATE) {
            listGuests.sort(Comparator.comparing(Guest::getDepartureDate));
        }
        return listGuests;
    }

    public List<Priceable> getPriceOfRoomsAndServicesWithSort(SortType sortType) {
        List<Priceable> catalog = new ArrayList<>();
        if (sortType == SortType.PRICE) {
            catalog.addAll(rooms.values());
            catalog.addAll(services.values());
            catalog.sort(Comparator.comparing(Priceable::getPrice));
        } else if (sortType == SortType.SECTION) {
            catalog.addAll(rooms.values());
            List<Service> tempCatalog = new ArrayList<>(services.values());
            catalog.sort(Comparator.comparing(Priceable::getPrice));
            tempCatalog.sort(Comparator.comparing(Service::getServiceSection));
            catalog.addAll(tempCatalog);
        }
        return catalog;
    }

    public List<Service> getServicesWithSort(SortType sortType) {
        List<Service> listServices = new ArrayList<>(services.values());
        if (sortType == SortType.PRICE) {
            listServices.sort(Comparator.comparing(Service::getPrice));
        } else if (sortType == SortType.SECTION) {
            listServices.sort(Comparator.comparing(Service::getServiceSection));
        }
        return listServices;
    }

    public Room getRoom(int number) {
        return rooms.get(number);
    }

    public Service getService(String name) {
        return services.get(name);
    }

    public boolean isFree(int number) {
        return rooms.get(number).getStatus() == Status.AVAILABLE;
    }

    public static long getMsecInDay() {
        return MSEC_IN_DAY;
    }

    public boolean isServicing(int number) {
        return rooms.get(number).getStatus() == Status.IN_SERVICE;
    }

    public boolean isOccupied(int number) {
        return rooms.get(number).getStatus() == Status.OCCUPIED;
    }
}
