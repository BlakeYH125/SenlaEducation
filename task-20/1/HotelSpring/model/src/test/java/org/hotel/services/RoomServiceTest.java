package org.hotel.services;

import org.hotel.model.entities.Guest;
import org.hotel.model.entities.Room;
import org.hotel.model.enums.RoomStatus;
import org.hotel.model.enums.SortType;
import org.hotel.model.exceptions.RoomNotFoundException;
import org.hotel.model.exceptions.RoomAlreadyExistsException;
import org.hotel.model.exceptions.ChangeStatusBannedException;
import org.hotel.model.exceptions.RoomAlreadyOccupiedException;
import org.hotel.model.exceptions.RoomAlreadyAvailableException;
import org.hotel.model.exceptions.RoomAlreadyInServiceException;
import org.hotel.model.exceptions.WrongSortTypeException;
import org.hotel.model.repository.GuestRepository;
import org.hotel.model.repository.RoomRepository;
import org.hotel.model.services.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void getRooms_ShouldReturnListOfRooms_WhenRoomsExists() {
        String roomId1 = "r1";
        String roomId2 = "r2";
        Room room1 = new Room();
        room1.setId(roomId1);
        Room room2 = new Room();
        room2.setId(roomId2);
        List<Room> expectedList = new ArrayList<>(List.of(room1, room2));

        when(roomRepository.findAll()).thenReturn(expectedList);

        List<Room> actualList = roomService.getRooms();

        assertEquals(expectedList, actualList);

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void getRooms_ShouldReturnEmptyList_WhenRoomsDoNotExists() {
        List<Room> expectedList = new ArrayList<>();

        when(roomRepository.findAll()).thenReturn(expectedList);

        List<Room> actualList = roomService.getRooms();

        assertEquals(expectedList, actualList);

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void isThereRoom_ShouldReturnTrue_WhenRoomExists() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean actualResult = roomService.isThereRoom(roomId);

        assertTrue(actualResult);

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void isThereRoom_ShouldReturnFalse_WhenRoomDoesNotExists() {
        String roomId = "222";

        when(roomRepository.getRoom(roomId)).thenReturn(null);

        boolean actualResult = roomService.isThereRoom(roomId);

        assertFalse(actualResult);

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void getRoom_ShouldReturnRoom_WhenRoomExists() {
        String roomId = "r205";
        Room expectedRoom = new Room();
        expectedRoom.setId(roomId);

        when(roomRepository.getRoom(roomId)).thenReturn(expectedRoom);

        Room actualRoom = roomService.getRoom(roomId);

        assertNotNull(actualRoom);
        assertEquals(roomId, actualRoom.getId());

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void getRoom_ShouldThrowException_WhenRoomDoesNotExist() {
        String badRoomId = "123";
        when(roomRepository.getRoom(badRoomId)).thenReturn(null);
        assertThrows(RoomNotFoundException.class, () -> roomService.getRoom(badRoomId));

        verify(roomRepository, times(1)).getRoom(badRoomId);
    }

    @Test
    void addNewRoom_ShouldSaveRoom_WhenRoomDoesNotExistsYet() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        roomService.addNewRoom(room);

        verify(roomRepository, times(1)).getRoom(roomId);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void addNewRoom_ShouldThrowRoomAlreadyExistsException_WhenRoomAlreadyExists() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyExistsException.class, () -> roomService.addNewRoom(room));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setAvailable_ShouldSetRoomAvailable_WhenAllCorrect() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        roomService.setAvailable(roomId);

        verify(roomRepository, times(7)).getRoom(roomId);
        verify(roomRepository, times(1)).setAvailable(room);
    }

    @Test
    void setAvailable_ShouldThrowRoomNotFoundException_WhenRoomDoesNotExists() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.setAvailable(roomId));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setAvailable_ShouldThrowChangeStatusBannedException_WhenChangeStatusBanned() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(ChangeStatusBannedException.class, () -> roomService.setAvailable(roomId));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setAvailable_ShouldThrowRoomAlreadyOccupiedException_WhenRoomOccupied() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.OCCUPIED);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyOccupiedException.class, () -> roomService.setAvailable(roomId));

        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void setAvailable_ShouldThrowRoomAlreadyAvailableException_WhenRoomAvailable() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyAvailableException.class, () -> roomService.setAvailable(roomId));

        verify(roomRepository, times(5)).getRoom(roomId);
    }

    @Test
    void setAvailableToEvict_ShouldSetRoomAvailable_WhenAllCorrect() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        roomService.setAvailableToEvict(roomId);

        verify(roomRepository, times(5)).getRoom(roomId);
        verify(roomRepository, times(1)).setAvailable(room);
    }

    @Test
    void setAvailableToEvict_ShouldThrowRoomNotFoundException_WhenRoomDoesNotExists() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.setAvailableToEvict(roomId));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setAvailableToEvict_ShouldThrowRoomAlreadyAvailableException_WhenRoomAvailable() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyAvailableException.class, () -> roomService.setAvailableToEvict(roomId));

        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void setOccupied_ShouldSetOccupied_WhenAllCorrect() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        roomService.setOccupied(roomId, daysCount);

        verify(roomRepository, times(1)).setStatus(
                eq(room),
                any(java.sql.Date.class),
                eq(RoomStatus.OCCUPIED)
        );

        verify(roomRepository, times(7)).getRoom(roomId);
    }

    @Test
    void setOccupied_ShouldThrowRoomNotFoundException_WhenRoomNotFound() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.setOccupied(roomId, daysCount));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setOccupied_ShouldThrowChangeStatusBannedException_WhenChangeStatusBanned() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(ChangeStatusBannedException.class, () -> roomService.setOccupied(roomId, daysCount));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setOccupied_ShouldThrowAlreadyInServiceException_WhenRoomInService() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyInServiceException.class, () -> roomService.setOccupied(roomId, daysCount));

        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void setOccupied_ShouldThrowRoomAlreadyOccupiedException_WhenRoomAlreadyOccupied() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.OCCUPIED);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyOccupiedException.class, () -> roomService.setOccupied(roomId, daysCount));

        verify(roomRepository, times(5)).getRoom(roomId);
    }

    @Test
    void setOccupiedToSettle_ShouldSetOccupied_WhenAllCorrect() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        roomService.setOccupiedToSettle(roomId, daysCount);

        verify(roomRepository, times(1)).setStatus(
                eq(room),
                any(java.sql.Date.class),
                eq(RoomStatus.OCCUPIED)
        );

        verify(roomRepository, times(7)).getRoom(roomId);
    }

    @Test
    void setOccupiedToSettle_ShouldThrowRoomNotFoundException_WhenRoomNotFound() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.setOccupiedToSettle(roomId, daysCount));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setOccupiedToSettle_ShouldThrowAlreadyInServiceException_WhenRoomInService() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyInServiceException.class, () -> roomService.setOccupied(roomId, daysCount));

        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void setOccupiedToSettle_ShouldThrowRoomAlreadyOccupiedException_WhenRoomAlreadyOccupied() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.OCCUPIED);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyOccupiedException.class, () -> roomService.setOccupiedToSettle(roomId, daysCount));

        verify(roomRepository, times(5)).getRoom(roomId);
    }

    @Test
    void setInService_ShouldSetInService_WhenAllCorrect() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        roomService.setInService(roomId, daysCount);

        verify(roomRepository, times(1)).setStatus(
                eq(room),
                any(java.sql.Date.class),
                eq(RoomStatus.IN_SERVICE)
        );

        verify(roomRepository, times(7)).getRoom(roomId);
    }

    @Test
    void setInService_ShouldThrowRoomNotFoundException_WhenRoomNotFound() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.setInService(roomId, daysCount));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setInService_ShouldThrowChangeStatusBannedException_WhenChangeStatusBanned() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(ChangeStatusBannedException.class, () -> roomService.setInService(roomId, daysCount));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setInService_ShouldThrowAlreadyInServiceException_WhenRoomAlreadyInService() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyInServiceException.class, () -> roomService.setInService(roomId, daysCount));

        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void setOccupied_ShouldThrowRoomAlreadyOccupiedException_WhenRoomOccupied() {
        String roomId = "123";
        int daysCount = 5;
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.OCCUPIED);
        ReflectionTestUtils.setField(roomService, "isAllowChange", true);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(RoomAlreadyOccupiedException.class, () -> roomService.setInService(roomId, daysCount));

        verify(roomRepository, times(5)).getRoom(roomId);
    }

    @Test
    void isAvailable_ReturnTrue_WhenStatusAvailable() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean result = roomService.isAvailable(roomId);

        assertTrue(result);

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void isAvailable_ReturnFalse_WhenStatusNotAvailable() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean result = roomService.isAvailable(roomId);

        assertFalse(result);

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void isServicing_ReturnTrue_WhenStatusInService() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean result = roomService.isServicing(roomId);

        assertTrue(result);

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void isAvailable_ReturnFalse_WhenStatusNotInService() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.AVAILABLE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean result = roomService.isServicing(roomId);

        assertFalse(result);

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void isOccupied_ReturnTrue_WhenStatusOccupied() {
        String roomId = "123";

        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.OCCUPIED);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean result = roomService.isOccupied(roomId);

        assertTrue(result);

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void isOccupied_ReturnFalse_WhenStatusNotOccupied() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);
        room.setStatus(RoomStatus.IN_SERVICE);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        boolean result = roomService.isOccupied(roomId);

        assertFalse(result);

        verify(roomRepository, times(2)).getRoom(roomId);
    }

    @Test
    void setNewRoomPrice_ChangeRoomPrice_WhenAllCorrect() {
        String roomId = "123";
        BigDecimal newPrice = new BigDecimal(150);
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        roomService.setNewRoomPrice(roomId, newPrice);

        verify(roomRepository, times(1)).setNewRoomPrice(room, newPrice);
        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void setNewRoomPrice_ThrowRoomNotFoundException_WhenRoomNotFound() {
        String roomId = "123";
        BigDecimal newPrice = new BigDecimal(150);
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.setNewRoomPrice(roomId, newPrice));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void setNewRoomPrice_ThrowIllegalArgumentException_WhenPriceLessZero() {
        String roomId = "123";
        BigDecimal newPrice = new BigDecimal(-42);
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);

        assertThrows(IllegalArgumentException.class, () -> roomService.setNewRoomPrice(roomId, newPrice));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void getThreePrevRoomGuests_ReturnListOfPreviousGuests_WhenAllCorrect() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);

        Guest guest1 = new Guest();
        guest1.setId("g1");
        Guest guest2 = new Guest();
        guest2.setId("g2");
        List<Guest> expected = new ArrayList<>(List.of(guest1, guest2));

        ReflectionTestUtils.setField(roomService, "previousGuestsLimit", 3);

        when(roomRepository.getRoom(room.getId())).thenReturn(room);
        when(guestRepository.findPreviousGuests(room, 3)).thenReturn(expected);

        List<Guest> actual = roomService.getThreePrevRoomGuests(roomId);

        assertEquals(expected, actual);

        verify(guestRepository, times(1)).findPreviousGuests(room, 3);

        verify(roomRepository, times(3)).getRoom(roomId);
    }

    @Test
    void getThreePrevRoomGuests_ThrowRoomNotFoundException_WhenRoomNotFound() {
        String roomId = "123";
        Room room = new Room();
        room.setId(roomId);

        ReflectionTestUtils.setField(roomService, "previousGuestsLimit", 3);

        when(roomRepository.getRoom(room.getId())).thenReturn(null);

        assertThrows(RoomNotFoundException.class, () -> roomService.getThreePrevRoomGuests(roomId));

        verify(roomRepository, times(1)).getRoom(roomId);
    }

    @Test
    void getFreeRoomCount_ReturnRoomsCount() {
        String roomId1 = "123";
        Room room1 = new Room();
        room1.setId(roomId1);
        String roomId2 = "456";
        Room room2 = new Room();
        room2.setId(roomId2);
        List<Room> expectedList = new ArrayList<>(List.of(room1, room2));
        int expectedNumber = expectedList.size();

        when(roomRepository.findFreeRoomsByDate(any(java.sql.Date.class))).thenReturn(expectedList);

        int actual = roomService.getFreeRoomsCount();

        assertEquals(expectedNumber, actual);

        verify(roomRepository, times(1)).findFreeRoomsByDate(any(java.sql.Date.class));
    }

    @Test
    void getFreeRoomsByDate_ReturnRoomByDate() {
        String roomId1 = "123";
        Room room1 = new Room();
        room1.setId(roomId1);
        String roomId2 = "456";
        Room room2 = new Room();
        room2.setId(roomId2);
        List<Room> expectedList = new ArrayList<>(List.of(room1, room2));

        when(roomRepository.findFreeRoomsByDate(any(java.sql.Date.class))).thenReturn(expectedList);

        List<Room> actualList = roomService.getFreeRoomsByDate(new java.sql.Date(1));

        assertEquals(expectedList, actualList);

        verify(roomRepository, times(1)).findFreeRoomsByDate(any(java.sql.Date.class));
    }

    @Test
    void getAllRoomsWithSort_ReturnAllRoomsWithSort_WhenAllCorrect() {
        String roomId1 = "123";
        Room room1 = new Room();
        room1.setId(roomId1);
        room1.setPrice(new BigDecimal(450));
        String roomId2 = "456";
        Room room2 = new Room();
        room2.setId(roomId2);
        room2.setPrice(new BigDecimal(300));
        List<Room> expectedList = new ArrayList<>(List.of(room2, room1));

        when(roomRepository.findAll()).thenReturn(new ArrayList<>(List.of(room1, room2)));

        List<Room> actualList = roomService.getAllRoomsWithSort(SortType.PRICE);

        assertEquals(expectedList, actualList);

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void getAllRoomsWithSort_ThrowWrongSortTypeException_WhenWrongSortType() {
        assertThrows(WrongSortTypeException.class, () -> roomService.getAllRoomsWithSort(SortType.ALPHABET));
    }

    @Test
    void getFreeRoomsWithSort_ReturnFreeRoomsWithSort_WhenAllCorrect() {
        String roomId1 = "123";
        Room room1 = new Room();
        room1.setId(roomId1);
        room1.setPrice(new BigDecimal(450));
        String roomId2 = "456";
        Room room2 = new Room();
        room2.setId(roomId2);
        room2.setPrice(new BigDecimal(300));
        List<Room> expectedList = new ArrayList<>(List.of(room2, room1));

        when(roomRepository.findFreeRoomsByDate(any(java.sql.Date.class))).thenReturn(new ArrayList<>(List.of(room1, room2)));

        List<Room> actualList = roomService.getFreeRoomsWithSort(SortType.PRICE);

        assertEquals(expectedList, actualList);

        verify(roomRepository, times(1)).findFreeRoomsByDate(any(java.sql.Date.class));
    }

    @Test
    void getFreeRoomsWithSort_ThrowWrongSortTypeException_WhenWrongSortType() {
        assertThrows(WrongSortTypeException.class, () -> roomService.getFreeRoomsWithSort(SortType.ALPHABET));
    }

    @Test
    void getCurrentGuests_ReturnGuestsCount_WhenAllCorrect() {
        String roomId1 = "123";
        Room room1 = new Room();
        room1.setId(roomId1);
        Guest guest = new Guest();
        guest.setId("g1");
        List<Guest> expected = new ArrayList<>(List.of(guest));

        when(guestRepository.findCurrentGuestsInRoom(room1)).thenReturn(expected);
        when(roomRepository.getRoom(roomId1)).thenReturn(room1);

        List<Guest> actual = roomService.getCurrentGuests(room1);

        assertEquals(expected, actual);

        verify(guestRepository, times(1)).findCurrentGuestsInRoom(room1);
        verify(roomRepository, times(1)).getRoom(roomId1);
    }

    @Test
    void importRooms_0Error1Successes_WhenAllCorrect() throws Exception {
        String csvContent = "123;101;5000;AVAILABLE;2;4\n";
        MockMultipartFile mockFile = new MockMultipartFile("file", "rooms.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        when(roomRepository.getRoom("123")).thenReturn(null);

        String actual = roomService.importRooms(mockFile);

        String expected = "Импорт завершен. Количество ошибок: 0, количество успешно считанных строк: 1";

        assertEquals(expected, actual);

        verify(roomRepository, times(1)).save(any(Room.class));

        verify(roomRepository, times(2)).getRoom("123");
    }

    @Test
    void importRooms_1Error0Successes_WhenWrongNumberOfParameters() throws Exception {
        String csvContent = "123;101;2;4\n";

        MockMultipartFile mockFile = new MockMultipartFile("file", "rooms.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        String actual = roomService.importRooms(mockFile);

        String expected = "Импорт завершен. Количество ошибок: 1, количество успешно считанных строк: 0";

        assertEquals(expected, actual);
    }

    @Test
    void importRooms_1Error0Successes_WhenRoomAlreadyExists() throws Exception{
        String csvContent = "123;101;5000;AVAILABLE;2;4\n";

        Room room = new Room();
        room.setId("r1");

        when(roomRepository.getRoom("r1")).thenReturn(room);

        MockMultipartFile mockFile = new MockMultipartFile("file", "services.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8));

        String actual = roomService.importRooms(mockFile);

        String expected = "Импорт завершен. Количество ошибок: 1, количество успешно считанных строк: 0";

        assertEquals(expected, actual);
    }

    @Test
    void exportRooms_ReturnCsvString_WhenAllCorrect() {
        Room room = new Room();
        room.setId("123");
        room.setNumber(101);
        room.setPrice(new BigDecimal("5000"));
        room.setStatus(RoomStatus.AVAILABLE);
        room.setCapacity(2);
        room.setStars(4);

        when(roomRepository.findAll()).thenReturn(new ArrayList<>(List.of(room)));

        String actual = roomService.exportRooms();
        String expected = "123;101;5000;AVAILABLE;2;4\n";

        assertEquals(expected, actual);

        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void exportRooms_ReturnEmptyLine_WhenDataBaseEmpty() {
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());

        String expected = "";
        String actual = roomService.exportRooms();

        assertEquals(expected, actual);

        verify(roomRepository, times(1)).findAll();
    }
}
