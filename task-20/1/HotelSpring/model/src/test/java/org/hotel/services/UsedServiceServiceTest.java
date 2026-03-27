package org.hotel.services;

import org.hotel.model.entities.Guest;
import org.hotel.model.entities.UsedService;
import org.hotel.model.enums.SortType;
import org.hotel.model.exceptions.GuestNotFoundException;
import org.hotel.model.exceptions.WrongSortTypeException;
import org.hotel.model.repository.UsedServiceRepository;
import org.hotel.model.services.GuestService;
import org.hotel.model.services.UsedServiceService;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UsedServiceServiceTest {

    @Mock
    private UsedServiceRepository usedServiceRepository;

    @Mock
    private GuestService guestService;

    @InjectMocks
    private UsedServiceService usedServiceService;

    @Test
    void addUsedService_SaveUsedService() {
        UsedService usedService = new UsedService();

        usedServiceService.addUsedService(usedService);

        verify(usedServiceRepository, times(1)).save(usedService);
    }

    @Test
    void getUsedServicesByGuestWithSort_ReturnSortedUsedServices_WhenAllCorrect() {
        UsedService us1 = new UsedService();
        us1.setPrice(new BigDecimal(500));
        UsedService us2 = new UsedService();
        us2.setPrice(new BigDecimal(450));

        List<UsedService> expected = new ArrayList<>(List.of(us2, us1));

        List<UsedService> actual = usedServiceService.getUsedServicesByGuestWithSort(new ArrayList<>(List.of(us1, us2)), SortType.PRICE);

        assertEquals(expected, actual);
    }

    @Test
    void getUsedServicesByGuestWithSort_ThrowWrongSortTypeException_WhenWrongSortType() {
        UsedService us1 = new UsedService();
        us1.setPrice(new BigDecimal(500));
        UsedService us2 = new UsedService();
        us2.setPrice(new BigDecimal(450));

        assertThrows(WrongSortTypeException.class, () -> usedServiceService.getUsedServicesByGuestWithSort(new ArrayList<>(List.of(us1, us2)), SortType.ALPHABET));
    }

    @Test
    void getUsedService_ReturnUsedServices_WhenAllCorrect() {
        Guest guest = new Guest();
        guest.setId("g1");

        UsedService us1 = new UsedService();
        UsedService us2 = new UsedService();

        when(guestService.isThereGuest("g1")).thenReturn(true);
        when(guestService.getGuest("g1")).thenReturn(guest);
        when(usedServiceRepository.findServicesUsedByGuest(guest)).thenReturn(new ArrayList<>(List.of(us1, us2)));

        List<UsedService> expected = new ArrayList<>(List.of(us1, us2));
        List<UsedService> actual = usedServiceService.getUsedServices("g1");

        assertEquals(expected, actual);

        verify(usedServiceRepository, times(1)).findServicesUsedByGuest(guest);
        verify(guestService, times(1)).isThereGuest("g1");
        verify(guestService, times(1)).getGuest("g1");
    }

    @Test
    void getUsedService_ThrowGuestNotFoundException_WhenGuestDoesNotExists() {
        when(guestService.isThereGuest("g1")).thenReturn(false);

        assertThrows(GuestNotFoundException.class, () -> usedServiceService.getUsedServices("g1"));
    }
}
