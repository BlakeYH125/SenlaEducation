import java.util.*;

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

    public void removeGuest(Guest guest) {
        guests.remove(guest);
    }

    public void addNewService(Service service) {
        services.put(service.getName(), service);
    }


    public void settle(int number, Guest guest, int daysCount) {
        Room room = rooms.get(number);
        if (room.getStatus() == Status.OCCUPIED) {
            System.out.println("В данный момент комната занята.");
        } else if (room.getStatus() == Status.IN_SERVICE) {
            System.out.println("В данный момент комната на обслуживании.");
        } else {
            room.setGuest(guest);
            setOccupied(number, daysCount);
            guest.setRentRoom(room);
            guest.setArriveDate(new Date(System.currentTimeMillis()));
            guest.setDepartureDate(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
            addGuest(guest);
            rooms.put(room.getNumber(), room);
        }
    }

    public void evict(int number) {
        Room room = rooms.get(number);
        Guest guest = room.getGuest();
        if (room.getStatus() == Status.AVAILABLE) {
            System.out.println("В данный момент комната свободна.");
        } else {
            room.addToPrevGuestsList();
            guest.setRentRoom(null);
            guest.setDepartureDate(new Date(System.currentTimeMillis()));

            setAvailable(number);
            rooms.put(room.getNumber(), room);
            removeGuest(guest);
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
        Guest guest = room.getGuest();
        return (guest.getDepartureDate().getTime() - guest.getArriveDate().getTime()) / MSEC_IN_DAY * room.getPrice();
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

    public List<Object> getPriceOfRoomsAndServicesWithSort(SortType sortType) {
        List<Object> catalog = new ArrayList<>();
        catalog.addAll(rooms.values());
        catalog.addAll(services.values());
        if (sortType == SortType.PRICE) {
            catalog.sort((o1, o2) -> {
                double price1 = (o1 instanceof Room) ? ((Room)o1).getPrice() : ((Service)o1).getPrice();
                double price2 = (o2 instanceof Room) ? ((Room)o2).getPrice() : ((Service)o2).getPrice();
                return Double.compare(price1, price2);
            });
        } else if (sortType == SortType.SECTION) {
            catalog.sort((o1, o2) -> {
                if (o1 instanceof Room && o2 instanceof Service) return -1;
                if (o1 instanceof Service && o2 instanceof Room) return 1;
                if (o1 instanceof Room) {
                    return Double.compare(((Room)o1).getPrice(), ((Room)o2).getPrice());
                }
                Service s1 = (Service)o1;
                Service s2 = (Service)o2;

                int sectionCompare = s1.getServiceSection().compareTo(s2.getServiceSection());
                if (sectionCompare != 0) {
                    return sectionCompare;
                }
                return Double.compare(s1.getPrice(), s2.getPrice());
            });
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
}
