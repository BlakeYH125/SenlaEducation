package org.hotel.model.management;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.constants.StatusConstants;
import org.hotel.constants.TimeConstants;
import org.hotel.model.Priceable;
import org.hotel.model.entities.Guest;
import org.hotel.model.entities.Room;
import org.hotel.model.entities.Service;
import org.hotel.model.entities.UsedService;
import org.hotel.model.enums.SortType;
import org.hotel.model.enums.Status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Component
public final class Administrator {
    /**
     * Класс управления гостями.
     */
    @Inject
    private GuestManagement guestManagement;

    /**
     * Класс управления комнатами.
     */
    @Inject
    private RoomManagement roomManagement;

    /**
     * Класс управления услугами.
     */
    @Inject
    private ServiceManagement serviceManagement;

    /**
     * Класс управления использованными услугами.
     */
    @Inject
    private UsedServiceManagement usedServiceManagement;

    public Administrator() {
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

    public UsedServiceManagement getUsedServiceManagement() {
        return usedServiceManagement;
    }

    public int settle(final String id, final List<Guest> guests, final int daysCount) {
        Room room = roomManagement.getRoom(id);
        if (room.getStatus() == Status.OCCUPIED) {
            return StatusConstants.OCCUPIED_STATUS;
        } else if (room.getStatus() == Status.IN_SERVICE) {
            return StatusConstants.IN_SERVICE_STATUS;
        } else {
            roomManagement.setOccupiedToSettle(id, daysCount);
            for (Guest guest : guests) {
                guest.setRentRoomId(room.getId());
                guest.setArriveDate(new Date(System.currentTimeMillis()));
                guest.setDepartureDate(new Date(System.currentTimeMillis() + daysCount * TimeConstants.MSEC_IN_DAY));
                guestManagement.addGuest(guest);
            }
            guestManagement.setGuests(guests);
            return 0;
        }
    }

    public boolean evict(final String id) {
        Room room = roomManagement.getRoom(id);
        List<Guest> guests = roomManagement.getCurrentGuests(room);
        if (room.getStatus() == Status.AVAILABLE || room.getStatus() == Status.IN_SERVICE) {
            return false;
        } else {
            for (Guest guest : guests) {
                guestManagement.setEvicted(guest.getId());
            }
            roomManagement.setAvailableToEvict(id);
            return true;
        }
    }

    public void useServiceByGuest(final String guestId, final String serviceId) {
        UsedService usedService = new UsedService(null, serviceId, guestId, serviceManagement.getServicePrice(serviceId), new Date());
        usedServiceManagement.addUsedService(usedService);
    }

    public List<Priceable> getPriceOfRoomsAndServicesWithSort(final SortType sortType) {
        List<Priceable> catalog = new ArrayList<>();
        if (sortType == SortType.PRICE) {
            catalog.addAll(roomManagement.getRooms());
            catalog.addAll(new ArrayList<>(serviceManagement.getServices()));
            catalog.sort(Comparator.comparing(Priceable::getPrice));
        } else if (sortType == SortType.SECTION) {
            catalog.addAll(new ArrayList<>(roomManagement.getRooms()));
            List<Service> tempCatalog = new ArrayList<>(serviceManagement.getServices());
            catalog.sort(Comparator.comparing(Priceable::getPrice));
            tempCatalog.sort(Comparator.comparing(Service::getServiceSection));
            catalog.addAll(tempCatalog);
        }
        return catalog;
    }
}
