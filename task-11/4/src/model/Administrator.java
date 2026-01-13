package model;

import annotations.Component;
import annotations.Inject;
import database.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Component
public class Administrator {
    private static final long MSEC_IN_DAY = 86400000;

    @Inject
    private GuestManagement guestManagement;

    @Inject
    private RoomManagement roomManagement;

    @Inject
    private ServiceManagement serviceManagement;

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

    public int settle(String id, List<Guest> guests, int daysCount) {
        Room room = roomManagement.getRoom(id);
        if (room.getStatus() == Status.OCCUPIED) {
            return -2;
        } else if (room.getStatus() == Status.IN_SERVICE) {
            return -1;
        } else {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                connection.setAutoCommit(false);
                roomManagement.setOccupiedToSettle(id, daysCount);
                for (Guest guest : guests) {
                    guest.setRentRoomId(room.getId());
                    guest.setArriveDate(new Date(System.currentTimeMillis()));
                    guest.setDepartureDate(new Date(System.currentTimeMillis() + daysCount * MSEC_IN_DAY));
                    guestManagement.addGuest(guest);
                }
                guestManagement.setGuests(guests);
                connection.commit();
                return 0;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return -3;
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException exe) {
                    exe.printStackTrace();
                }
            }
        }
    }

    public boolean evict(String id) {
        Room room = roomManagement.getRoom(id);
        List<Guest> guests = roomManagement.getCurrentGuests(room);
        if (room.getStatus() == Status.AVAILABLE || room.getStatus() == Status.IN_SERVICE) {
            return false;
        } else {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                connection.setAutoCommit(false);
                for (Guest guest : guests) {
                    guestManagement.setEvicted(guest.getId());
                }
                roomManagement.setAvailableToEvict(id);
                connection.commit();
                return true;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return false;

            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException exe) {
                    exe.printStackTrace();
                }
            }
        }
    }

    public void useServiceByGuest(String guestId, String serviceId) {
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);
            UsedService usedService = new UsedService(null, serviceId, guestId, serviceManagement.getServicePrice(serviceId), new Date());
            usedServiceManagement.addUsedService(usedService);
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException exe) {
                exe.printStackTrace();
            }
        }
    }

    public List<Priceable> getPriceOfRoomsAndServicesWithSort(SortType sortType) {
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
