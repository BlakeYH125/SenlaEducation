package org.hotel.services;

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
import org.hotel.model.services.GuestService;
import org.hotel.model.services.RoomService;
import org.hotel.model.services.ServiceService;
import org.hotel.model.services.UsedServiceService;
import org.hotel.model.services.AdministratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdministratorServiceTest {

    @Mock
    private GuestService guestService;

    @Mock
    private RoomService roomService;

    @Mock
    private ServiceService serviceService;

    @Mock
    private UsedServiceService usedServiceService;

    @InjectMocks
    private AdministratorService administratorService;

    @Test
    void settle_ShouldUpdateRoomAndGuests_WhenAllCorrect() {
        int daysCount = 5;
        String roomId = "r1";

        Room room = new Room();
        room.setId(roomId);

        Guest guest = new Guest();
        List<Guest> guests = new ArrayList<>(List.of(guest));

        administratorService.settle(roomId, guests, daysCount);

        verify(roomService, times(1)).setOccupiedToSettle(roomId, daysCount);

        assertEquals(roomId, guest.getRentRoomId());
        assertEquals(GuestStatus.SETTLED, guest.getStatus());
        assertNotNull(guest.getArriveDate());
        assertNotNull(guest.getDepartureDate());

        verify(guestService, times(1)).addGuest(guest);
    }

    @Test
    void evict_ShouldUpdateRoomAndEvictGuests_WhenAllCorrect() {
        String roomId = "r1";
        Room room = new Room();
        room.setId(roomId);

        Guest guest1 = new Guest();
        guest1.setId("g1");
        Guest guest2 = new Guest();
        guest2.setId("g2");
        List<Guest> guests = new ArrayList<>(List.of(guest1, guest2));

        when(roomService.getRoom(roomId)).thenReturn(room);
        when(roomService.getCurrentGuests(room)).thenReturn(guests);

        administratorService.evict(roomId);

        verify(roomService, times(1)).setAvailableToEvict(roomId);

        verify(roomService, times(1)).getRoom(roomId);
        verify(roomService, times(1)).getCurrentGuests(room);

        verify(guestService, times(1)).setEvicted("g1");
        verify(guestService, times(1)).setEvicted("g2");
    }

    @Test
    void useServiceByGuest_ShouldSaveUsedService_WhenAllCorrect() {
        Service service = new Service();
        service.setId("s1");

        Guest guest = new Guest();
        guest.setId("g1");

        when(guestService.isThereGuest("g1")).thenReturn(true);
        when(serviceService.isThereService("s1")).thenReturn(true);

        administratorService.useServiceByGuest("g1", "s1");

        verify(usedServiceService, times(1)).addUsedService(any(UsedService.class));
    }

    @Test
    void useServiceByGuest_ShouldThrowGuestNotFoundException_WhenGuestDoesNotExists() {
        Service service = new Service();
        service.setId("s1");

        Guest guest = new Guest();
        guest.setId("g1");

        when(guestService.isThereGuest("g1")).thenReturn(false);

        assertThrows(GuestNotFoundException.class, () -> administratorService.useServiceByGuest("g1", "s1"));
    }

    @Test
    void useServiceByGuest_ShouldThrowServiceNotFoundException_WhenServiceDoesNotExists() {
        Service service = new Service();
        service.setId("s1");

        Guest guest = new Guest();
        guest.setId("g1");

        when(guestService.isThereGuest("g1")).thenReturn(true);
        when(serviceService.isThereService("s1")).thenReturn(false);

        assertThrows(ServiceNotFoundException.class, () -> administratorService.useServiceByGuest("g1", "s1"));
    }

    @Test
    void getPriceOfRoomsAndServicesWithSort_ShouldReturnCatalog_WhenAllCorrect() {
        Room room = new Room();
        room.setId("r1");
        room.setPrice(new BigDecimal(2000));

        Service service = new Service();
        service.setId("s1");
        service.setPrice(new BigDecimal(500));

        when(roomService.getRooms()).thenReturn(new ArrayList<>(List.of(room)));
        when(serviceService.getServices()).thenReturn(new ArrayList<>(List.of(service)));

        List<Priceable> expected = new ArrayList<>(List.of(service, room));

        List<Priceable> actual = administratorService.getPriceOfRoomsAndServicesWithSort(SortType.PRICE);

        assertEquals(expected, actual);

        verify(roomService, times(1)).getRooms();
        verify(serviceService, times(1)).getServices();
    }

    @Test
    void getPriceOfRoomsAndServicesWithSort_ShouldThrowWrongSortTypeException_WhenWrongSortType() {
        Room room = new Room();
        room.setId("r1");
        room.setPrice(new BigDecimal(2000));

        Service service = new Service();
        service.setId("s1");
        service.setPrice(new BigDecimal(500));

        assertThrows(WrongSortTypeException.class, () -> administratorService.getPriceOfRoomsAndServicesWithSort(SortType.ALPHABET));
    }
}
