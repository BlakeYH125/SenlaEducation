package org.hotel.model.services;

import jakarta.transaction.Transactional;
import org.hotel.constants.TimeConstants;
import org.hotel.model.Priceable;
import org.hotel.model.entities.Guest;
import org.hotel.model.entities.Room;
import org.hotel.model.entities.Service;
import org.hotel.model.entities.UsedService;
import org.hotel.model.enums.GuestStatus;
import org.hotel.model.enums.SortType;
import org.hotel.model.exceptions.GuestNotFoundException;
import org.hotel.model.exceptions.ServiceNotFoundException;
import org.hotel.model.exceptions.WrongSortTypeException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@org.springframework.stereotype.Service
@Transactional
public class AdministratorService {
    /**
     * Класс управления гостями.
     */
    private final GuestService guestService;

    /**
     * Класс управления комнатами.
     */
    private final RoomService roomService;

    /**
     * Класс управления услугами.
     */
    private final ServiceService serviceService;

    /**
     * Класс управления использованными услугами.
     */
    private final UsedServiceService usedServiceService;

    public AdministratorService(final GuestService guestServiceP, final RoomService roomServiceP, final ServiceService serviceServiceP, final UsedServiceService usedServiceServiceP) {
        this.guestService = guestServiceP;
        this.roomService = roomServiceP;
        this.serviceService = serviceServiceP;
        this.usedServiceService = usedServiceServiceP;
    }

    public void settle(final String roomIdP, final List<Guest> guests, final int daysCount) {
        roomService.setOccupiedToSettle(roomIdP, daysCount);
        Date arriveDate = new Date(System.currentTimeMillis());
        Date departureDate = new Date(System.currentTimeMillis() + daysCount * TimeConstants.MSEC_IN_DAY);
        for (Guest guest : guests) {
            guest.setRentRoomId(roomIdP);
            guest.setArriveDate(arriveDate);
            guest.setDepartureDate(departureDate);
            guest.setStatus(GuestStatus.SETTLED);
            guestService.addGuest(guest);
        }
    }

    public void evict(final String roomIdP) {
        roomService.setAvailableToEvict(roomIdP);
        Room room = roomService.getRoom(roomIdP);
        List<Guest> guests = roomService.getCurrentGuests(room);
        for (Guest guest : guests) {
            guestService.setEvicted(guest.getId());
        }
    }

    public void useServiceByGuest(final String guestId, final String serviceId) {
        if (!guestService.isThereGuest(guestId)) {
            throw new GuestNotFoundException();
        }
        if (!serviceService.isThereService(serviceId)) {
            throw new ServiceNotFoundException();
        }
        UsedService usedService = new UsedService(serviceId, guestId, serviceService.getServicePrice(serviceId), new Date());
        usedServiceService.addUsedService(usedService);
    }

    public List<Priceable> getPriceOfRoomsAndServicesWithSort(final SortType sortType) {
        if (sortType != SortType.PRICE && sortType != SortType.SECTION) {
            throw new WrongSortTypeException();
        }
        List<Priceable> catalog = new ArrayList<>();
        if (sortType == SortType.PRICE) {
            catalog.addAll(roomService.getRooms());
            catalog.addAll(new ArrayList<>(serviceService.getServices()));
            catalog.sort(Comparator.comparing(Priceable::getPrice));
        } else if (sortType == SortType.SECTION) {
            catalog.addAll(new ArrayList<>(roomService.getRooms()));
            List<Service> tempCatalog = new ArrayList<>(serviceService.getServices());
            catalog.sort(Comparator.comparing(Priceable::getPrice));
            tempCatalog.sort(Comparator.comparing(Service::getServiceSection));
            catalog.addAll(tempCatalog);
        }
        return catalog;
    }
}
