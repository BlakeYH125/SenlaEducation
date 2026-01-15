package org.hotel.model;

import org.hotel.annotations.Component;
import org.hotel.annotations.ConfigProperty;
import org.hotel.annotations.Inject;
import org.hotel.configurator.Configurator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


@Component
public final class RoomManagement {
    /**
     * 3 гостя для метода.
     */
    private static final int THREE_GUESTS = 3;

    /**
     * Количество миллисекунд в дне.
     */
    private static final long MSEC_IN_DAY = 86400000;

    /**
     * Репозиторий для работы с комнатами в БД.
     */
    @Inject
    private RoomRepository roomRepository;

    /**
     * Репозиторий для работы с гостями в БД.
     */
    @Inject
    private GuestRepository guestRepository;

    /**
     * Можно ли менять статус комнаты вручную.
     */
    @ConfigProperty(propertyName = "hotel.room.status.changing")
    private boolean isAllowChange;

    /**
     * Лимит отображения предыдущих гостей комнаты.
     */
    @ConfigProperty(propertyName = "hotel.room.history.limit")
    private int previousGuestsLimit;

    public RoomManagement() {
        Configurator.configure(this);
    }

    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    public boolean isThereRoom(final String idP) {
        if (roomRepository.getRoom(idP) == null) {
            return false;
        }
        return true;
    }

    public Room getRoom(final String idP) {
        return roomRepository.getRoom(idP);
    }

    public void addNewRoom(final Room roomP) {
        roomRepository.save(roomP);
    }

    public boolean setAvailable(final String idP) {
        if (isAllowChange) {
            roomRepository.setAvailable(getRoom(idP));
            return true;
        }
        return false;
    }

    public void setAvailableToEvict(final String idP) {
        roomRepository.setAvailable(getRoom(idP));
    }

    public boolean setStatus(final String idP, final int daysCountP, final Status statusP) {
        if (isAllowChange) {
            roomRepository.setStatus(getRoom(idP), new java.sql.Date(System.currentTimeMillis() + daysCountP * MSEC_IN_DAY), statusP);
            return true;
        }
        return false;
    }

    public void setOccupiedToSettle(final String idP, final int daysCountP) {
        roomRepository.setStatus(getRoom(idP), new java.sql.Date(System.currentTimeMillis() + daysCountP * MSEC_IN_DAY), Status.OCCUPIED);
    }

    public boolean isFree(final String idP) {
        return getRoom(idP).getStatus() == Status.AVAILABLE;
    }

    public boolean isServicing(final String idP) {
        return getRoom(idP).getStatus() == Status.IN_SERVICE;
    }

    public boolean isOccupied(final String idP) {
        return getRoom(idP).getStatus() == Status.OCCUPIED;
    }

    public static long getMSecInDay() {
        return MSEC_IN_DAY;
    }

    public void setNewRoomPrice(final String idP, final BigDecimal newPriceP) {
        roomRepository.setNewRoomPrice(getRoom(idP), newPriceP);
    }

    public List<Guest> getThreePrevRoomGuests(final String idP) {
        return guestRepository.findPreviousGuests(getRoom(idP), Math.min(THREE_GUESTS, previousGuestsLimit));
    }

    public BigDecimal getTotalRoomCost(final String idP) {
        Room room = getRoom(idP);
        List<Guest> guests = guestRepository.findCurrentGuestsInRoom(getRoom(idP));
        long millis = guests.get(0).getDepartureDate().getTime() - guests.get(0).getArriveDate().getTime();
        BigDecimal days = BigDecimal.valueOf(millis).divide(BigDecimal.valueOf(MSEC_IN_DAY), 2, RoundingMode.HALF_UP);
        return room.getPrice().multiply(days);
    }

    public int getFreeRoomsCount() {
        return new ArrayList<>(getFreeRoomsByDate(new Date(System.currentTimeMillis()))).size();
    }

    public List<Room> getFreeRoomsByDate(final Date dateP) {
        return roomRepository.findFreeRoomsByDate(new java.sql.Date(dateP.getTime()));
    }

    public String getRoomDetails(final String idP) {
        Room room = roomRepository.getRoom(idP);
        return room.toString();
    }

    public List<Room> getAllRoomsWithSort(final SortType sortTypeP) {
        List<Room> listRooms = new ArrayList<>(getRooms());
        if (sortTypeP == SortType.PRICE) {
            listRooms.sort(Comparator.comparing(Room::getPrice));
        } else if (sortTypeP == SortType.CAPACITY) {
            listRooms.sort(Comparator.comparing(Room::getCapacity));
        } else if (sortTypeP == SortType.STARS) {
            listRooms.sort(Comparator.comparing(Room::getStars));
        }
        return listRooms;
    }

    public List<Room> getFreeRoomsWithSort(final SortType sortTypeP) {
        List<Room> listRooms = new ArrayList<>(getFreeRoomsByDate(new Date(System.currentTimeMillis())));
        if (sortTypeP == SortType.PRICE) {
            listRooms.sort(Comparator.comparing(Room::getPrice));
        } else if (sortTypeP == SortType.CAPACITY) {
            listRooms.sort(Comparator.comparing(Room::getCapacity));
        } else if (sortTypeP == SortType.STARS) {
            listRooms.sort(Comparator.comparing(Room::getStars));
        }
        return listRooms;
    }

    public List<Guest> getCurrentGuests(final Room roomP) {
        return guestRepository.findCurrentGuestsInRoom(roomP);
    }
}
