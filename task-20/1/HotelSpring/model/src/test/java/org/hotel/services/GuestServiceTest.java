package org.hotel.services;

import org.hotel.constants.TimeConstants;
import org.hotel.model.entities.Guest;
import org.hotel.model.entities.Room;
import org.hotel.model.entities.User;
import org.hotel.model.enums.GuestStatus;
import org.hotel.model.enums.RoomStatus;
import org.hotel.model.exceptions.GuestNotFoundException;
import org.hotel.model.exceptions.GuestAlreadyEvictedException;
import org.hotel.model.exceptions.GuestAlreadyExistsException;
import org.hotel.model.exceptions.RoomNotFoundException;
import org.hotel.model.exceptions.RoomNotOccupiedException;
import org.hotel.model.repository.GuestRepository;
import org.hotel.model.services.GuestService;
import org.hotel.model.services.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private GuestService guestService;


    @Test
    void getGuestsCount_ReturnGuestCounts() {
        Guest g1 = new Guest();
        g1.setId("g1");
        Guest g2 = new Guest();
        g2.setId("g2");
        List<Guest> guests = new ArrayList<>(List.of(g1, g2));

        when(guestRepository.findCurrentGuestsInHotel()).thenReturn(guests);

        int actual = guestService.getGuestsCount();

        assertEquals(guests.size(), actual);

        verify(guestRepository, times(1)).findCurrentGuestsInHotel();
    }

    @Test
    void addGuest_SaveGuest_WhenAllCorrect() {
        Guest guest = new Guest();
        guest.setId("g1");

        when(guestRepository.getGuest("g1")).thenReturn(null);

        guestService.addGuest(guest);

        verify(guestRepository, times(1)).getGuest("g1");
        verify(guestRepository, times(1)).save(guest);
    }

    @Test
    void addGuest_ThrowGuestAlreadyExistsException_WhenGuestAlreadyExists() {
        Guest guest = new Guest();
        guest.setId("g1");

        when(guestRepository.getGuest("g1")).thenReturn(guest);

        assertThrows(GuestAlreadyExistsException.class, () -> guestService.addGuest(guest));

        verify(guestRepository, times(1)).getGuest("g1");
    }

    @Test
    void getGuest_ReturnGuest_WhenAllCorrect() {
        Guest guest = new Guest();
        guest.setId("g1");

        when(guestRepository.getGuest("g1")).thenReturn(guest);

        Guest actual = guestService.getGuest("g1");

        assertNotNull(actual);
        assertEquals(guest, actual);

        verify(guestRepository, times(2)).getGuest("g1");
    }

    @Test
    void getGuest_ThrowGuestNotFoundException_WhenGuestDoesNotExists() {
        Guest guest = new Guest();
        guest.setId("g1");

        when(guestRepository.getGuest("g1")).thenReturn(null);

        assertThrows(GuestNotFoundException.class, () -> guestService.getGuest("g1"));

        verify(guestRepository, times(1)).getGuest("g1");
    }

    @Test
    void setEvicted_SetGuestEvicted_WhenAllCorrect() {
        Guest guest = new Guest();
        guest.setId("g1");
        guest.setStatus(GuestStatus.SETTLED);

        when(guestRepository.getGuest("g1")).thenReturn(guest);

        guestService.setEvicted("g1");

        verify(guestRepository, times(1)).setEvicted(guest);
        verify(guestRepository, times(2)).getGuest("g1");
    }

    @Test
    void setEvicted_ThrowGuestNotFoundException_WhenGuestDoesNotExists() {
        Guest guest = new Guest();
        guest.setId("g1");
        guest.setStatus(GuestStatus.SETTLED);

        when(guestRepository.getGuest("g1")).thenReturn(null);

        assertThrows(GuestNotFoundException.class, () -> guestService.setEvicted("g1"));

        verify(guestRepository, times(1)).getGuest("g1");
    }

    @Test
    void setEvicted_ThrowGuestAlreadyEvictedException_WhenGuestAlreadyEvicted() {
        Guest guest = new Guest();
        guest.setId("g1");
        guest.setStatus(GuestStatus.EVICTED);

        when(guestRepository.getGuest("g1")).thenReturn(guest);

        assertThrows(GuestAlreadyEvictedException.class, () -> guestService.setEvicted("g1"));

        verify(guestRepository, times(2)).getGuest("g1");
    }

    @Test
    void getActualGuests_ReturnActualGuestsList_WhenAllCorrect() {
        Guest g1 = new Guest();
        g1.setId("g1");
        g1.setStatus(GuestStatus.SETTLED);
        Guest g2 = new Guest();
        g2.setId("g2");
        g2.setStatus(GuestStatus.SETTLED);
        List<Guest> expected = new ArrayList<>(List.of(g1, g2));

        when(guestRepository.findCurrentGuestsInHotel()).thenReturn(expected);

        List<Guest> actual = guestService.getActualGuests();

        assertEquals(expected, actual);

        verify(guestRepository, times(1)).findCurrentGuestsInHotel();
    }

    @Test
    void isThereGuest_ReturnTrue_WhenGuestExists() {
        Guest guest = new Guest();
        guest.setId("g1");

        when(guestRepository.getGuest("g1")).thenReturn(guest);

        boolean result = guestService.isThereGuest("g1");

        assertTrue(result);

        verify(guestRepository, times(1)).getGuest("g1");
    }

    @Test
    void isThereGuest_ReturnFalse_WhenGuestDoesNotExists() {
        Guest guest = new Guest();
        guest.setId("g1");

        when(guestRepository.getGuest("g1")).thenReturn(null);

        boolean result = guestService.isThereGuest("g1");

        assertFalse(result);

        verify(guestRepository, times(1)).getGuest("g1");
    }

    @Test
    void importGuests_0Error1Successes_WhenAllCorrect() throws Exception {
        String csv = "g1;Vasiliy Petrov;18\n";
        MultipartFile mockFile = new MockMultipartFile("file", "guests.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(guestRepository.getGuest("g1")).thenReturn(null);

        String expected = "Импорт успешен. Количество ошибок: 0, количество успешно считанных строк: 1";

        String actual = guestService.importGuests(mockFile);

        assertEquals(expected, actual);

        verify(guestRepository, times(1)).save(any(Guest.class));
    }

    @Test
    void importGuests_1Error0Successes_WhenWrongNumberOfParameters() throws Exception {
        String csv = "g1;18\n";
        MultipartFile mockFile = new MockMultipartFile("file", "guests.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        String expected = "Импорт успешен. Количество ошибок: 1, количество успешно считанных строк: 0";

        String actual = guestService.importGuests(mockFile);

        assertEquals(expected, actual);
    }

    @Test
    void exportGuests_ReturnCsvLine_WhenAllCorrect() {
        Guest guest = new Guest("g1", "Vasiliy Petrov", 18);

        when(guestRepository.findCurrentGuestsInHotel()).thenReturn(new ArrayList<>(List.of(guest)));

        String expected = "g1;Vasiliy Petrov;18\n";

        String actual = guestService.exportGuests();

        assertEquals(expected, actual);

        verify(guestRepository, times(1)).findCurrentGuestsInHotel();
    }

    @Test
    void exportGuests_ReturnEmptyLine_WhenDBIsEmpty() {
        Guest guest = new Guest("g1", "Vasiliy Petrov", 18);

        when(guestRepository.findCurrentGuestsInHotel()).thenReturn(new ArrayList<>());

        String expected = "";

        String actual = guestService.exportGuests();

        assertEquals(expected, actual);

        verify(guestRepository, times(1)).findCurrentGuestsInHotel();
    }

    @Test
    void getTotalCost_ReturnTotalCost_WhenAllCorrect() {
        Room room = new Room();
        room.setId("r1");
        room.setStatus(RoomStatus.OCCUPIED);
        room.setPrice(new BigDecimal(500));

        Guest guest = new Guest();
        guest.setId("g1");
        guest.setArriveDate(new Date(5 * TimeConstants.MSEC_IN_DAY));
        guest.setDepartureDate(new Date(6 * TimeConstants.MSEC_IN_DAY));

        when(roomService.isThereRoom("r1")).thenReturn(true);
        when(roomService.isOccupied("r1")).thenReturn(true);
        when(roomService.getRoom("r1")).thenReturn(room);

        when(guestRepository.findCurrentGuestsInRoom(room)).thenReturn(new ArrayList<>(List.of(guest)));

        BigDecimal expected = new BigDecimal(500).setScale(2, RoundingMode.HALF_UP);

        BigDecimal actual = guestService.getTotalCost("r1");

        assertEquals(expected, actual);

        verify(roomService, times(1)).isThereRoom("r1");
        verify(roomService, times(1)).isOccupied("r1");
        verify(roomService, times(1)).getRoom("r1");
        verify(guestRepository, times(1)).findCurrentGuestsInRoom(room);
    }

    @Test
    void getTotalCost_ThrowRoomNotFoundException_WhenRoomDoesNotExists() {
        Room room = new Room();
        room.setId("r1");
        room.setStatus(RoomStatus.OCCUPIED);
        room.setPrice(new BigDecimal(500));

        Guest guest = new Guest();
        guest.setId("g1");
        guest.setArriveDate(new Date(5 * TimeConstants.MSEC_IN_DAY));
        guest.setDepartureDate(new Date(6 * TimeConstants.MSEC_IN_DAY));

        when(roomService.isThereRoom("r1")).thenReturn(false);


        assertThrows(RoomNotFoundException.class, () -> guestService.getTotalCost("r1"));

        verify(roomService, times(1)).isThereRoom("r1");
    }

    @Test
    void getTotalCost_ThrowRoomNotOccupiedException_WhenRoomDoesNotOccupied() {
        Room room = new Room();
        room.setId("r1");
        room.setStatus(RoomStatus.AVAILABLE);
        room.setPrice(new BigDecimal(500));

        Guest guest = new Guest();
        guest.setId("g1");
        guest.setArriveDate(new Date(5 * TimeConstants.MSEC_IN_DAY));
        guest.setDepartureDate(new Date(6 * TimeConstants.MSEC_IN_DAY));

        when(roomService.isThereRoom("r1")).thenReturn(true);
        when(roomService.isOccupied("r1")).thenReturn(false);

        assertThrows(RoomNotOccupiedException.class, () -> guestService.getTotalCost("r1"));

        verify(roomService, times(1)).isThereRoom("r1");
        verify(roomService, times(1)).isOccupied("r1");
    }

    @Test
    void getTotalCost_ThrowGuestNotFoundException_WhenGuestDoesNotExists() {
        Room room = new Room();
        room.setId("r1");
        room.setStatus(RoomStatus.OCCUPIED);
        room.setPrice(new BigDecimal(500));

        Guest guest = new Guest();
        guest.setId("g1");
        guest.setArriveDate(new Date(5 * TimeConstants.MSEC_IN_DAY));
        guest.setDepartureDate(new Date(6 * TimeConstants.MSEC_IN_DAY));

        when(roomService.isThereRoom("r1")).thenReturn(true);
        when(roomService.isOccupied("r1")).thenReturn(true);
        when(roomService.getRoom("r1")).thenReturn(room);

        when(guestRepository.findCurrentGuestsInRoom(room)).thenReturn(new ArrayList<>());

        assertThrows(GuestNotFoundException.class, () -> guestService.getTotalCost("r1"));

        verify(roomService, times(1)).isThereRoom("r1");
        verify(roomService, times(1)).isOccupied("r1");
        verify(roomService, times(1)).getRoom("r1");
    }

    @Test
    void isRoomBelongsToUser_ShouldReturnTrue_WhenUserOwnsGuestInRoom() {
        String username = "testUser";
        String roomId = "r1";

        Room room = new Room();
        room.setId(roomId);

        User user = new User();
        user.setUsername(username);

        Guest guest = new Guest();
        guest.setId("g1");
        guest.setUser(user);

        when(roomService.isThereRoom(roomId)).thenReturn(true);
        when(roomService.isOccupied(roomId)).thenReturn(true);
        when(roomService.getRoom(roomId)).thenReturn(room);
        when(guestRepository.findCurrentGuestsInRoom(room)).thenReturn(new ArrayList<>(List.of(guest)));

        boolean actual = guestService.isRoomBelongsToUser(username, roomId);

        assertTrue(actual);
    }

    @Test
    void isRoomBelongsToUser_ShouldReturnFalse_WhenRoomDoesNotExistsOrNotOccupied() {
        String username = "testUser";
        String roomId = "r1";

        when(roomService.isThereRoom(roomId)).thenReturn(false);

        boolean actual = guestService.isRoomBelongsToUser(username, roomId);

        assertFalse(actual);
    }
}
