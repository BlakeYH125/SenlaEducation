import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Administrator {
    private static final long MSEC_IN_DAY = 86400000;
    private GuestManagement guestManagement;
    private RoomManagement roomManagement;
    private ServiceManagement serviceManagement;

    public Administrator() {
        this.guestManagement = new GuestManagement();
        this.roomManagement = new RoomManagement();
        this.serviceManagement = new ServiceManagement();
    }

    public GuestManagement getGuestManagement() {
        return guestManagement;
    }

    public RoomManagement getRoomManagement() {
        return roomManagement;
    }

    public ServiceManagement getServiceManagement() {
        return serviceManagement;
    }

    public int settle(String id, List<Guest> guests, int daysCount) {
        Room room = roomManagement.getRoom(id);
        if (room.getStatus() == Status.OCCUPIED) {
            return -2;
        } else if (room.getStatus() == Status.IN_SERVICE) {
            return -1;
        } else {
            room.setGuests(guests);
            roomManagement.setOccupied(id, daysCount);
            for (Guest guest : guests) {
                guest.setRentRoom(room);
                guest.setArriveDate(new Date(System.currentTimeMillis()));
                guest.setDepartureDate(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
                guestManagement.addGuest(guest);
            }
            roomManagement.addNewRoom(room);
            return 0;
        }
    }

    public boolean evict(String id) {
        Room room = roomManagement.getRoom(id);
        List<Guest> guests = room.getGuests();
        if (room.getStatus() == Status.AVAILABLE || room.getStatus() == Status.IN_SERVICE) {
            return false;
        } else {
            room.addToPrevGuestsList();
            for (Guest guest : guests) {
                guest.setRentRoom(null);
                guest.setDepartureDate(new Date(System.currentTimeMillis()));
                guestManagement.removeGuest(guest.getId());
            }
            roomManagement.setAvailable(id);
            roomManagement.addNewRoom(room);
            return true;
        }
    }

    public List<Priceable> getPriceOfRoomsAndServicesWithSort(SortType sortType) {
        List<Priceable> catalog = new ArrayList<>();
        if (sortType == SortType.PRICE) {
            catalog.addAll(roomManagement.getRooms().values());
            catalog.addAll(new ArrayList<>(serviceManagement.getServices().values()));
            catalog.sort(Comparator.comparing(Priceable::getPrice));
        } else if (sortType == SortType.SECTION) {
            catalog.addAll(new ArrayList<>(roomManagement.getRooms().values()));
            List<Service> tempCatalog = new ArrayList<>(serviceManagement.getServices().values());
            catalog.sort(Comparator.comparing(Priceable::getPrice));
            tempCatalog.sort(Comparator.comparing(Service::getServiceSection));
            catalog.addAll(tempCatalog);
        }
        return catalog;
    }
}
