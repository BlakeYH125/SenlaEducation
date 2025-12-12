package model;

import java.io.Serializable;

public class HotelState implements Serializable {
    private GuestManagement guestManagement;
    private RoomManagement roomManagement;
    private ServiceManagement serviceManagement;

    public HotelState(GuestManagement gm, RoomManagement rm, ServiceManagement sm) {
        this.guestManagement = gm;
        this.roomManagement = rm;
        this.serviceManagement = sm;
    }

    public GuestManagement getGuestManagement() { return guestManagement; }
    public RoomManagement getRoomManagement() { return roomManagement; }
    public ServiceManagement getServiceManagement() { return serviceManagement; }
}
