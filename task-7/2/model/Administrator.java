package model;

import java.util.*;

public class Administrator {
    private static final long MSEC_IN_DAY = 86400000;
    private GuestManagement guestManagement;
    private RoomManagement roomManagement;
    private ServiceManagement serviceManagement;
    transient private Properties settings;

    public Administrator(Properties settings) {
        this.guestManagement = new GuestManagement();
        this.roomManagement = new RoomManagement(settings);
        this.serviceManagement = new ServiceManagement();
        this.settings = settings;
    }

    public void setGuestManagement(GuestManagement guestManagement) {
        this.guestManagement = guestManagement;
    }

    public void setRoomManagement(RoomManagement roomManagement) {
        this.roomManagement = roomManagement;
    }

    public void setServiceManagement(ServiceManagement serviceManagement) {
        this.serviceManagement = serviceManagement;
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
            roomManagement.setOccupiedToSettle(id, daysCount);
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
            room.addToPrevGuestsList(settings);
            for (Guest guest : guests) {
                guest.setRentRoom(null);
                guest.setDepartureDate(new Date(System.currentTimeMillis()));
                guestManagement.removeGuest(guest.getId());
            }
            roomManagement.setAvailableToEvict(id);
            roomManagement.addNewRoom(room);
            return true;
        }
    }

    public void useServiceByGuest(String guestId, String serviceId) {
        Guest guest = guestManagement.getGuest(guestId);
        guest.addUsedService(serviceId, serviceManagement.getServiceName(serviceId), serviceManagement.getServicePrice(serviceId));
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
