package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Guest extends Person implements Serializable {
    private String id;
    private Room rentRoom;
    private Date arriveDate;
    private Date departureDate;
    private List<UsedService> usedServices;

    public Guest(String id, String fullName, int age) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
        this.usedServices = new ArrayList<>();
    }

    public void addUsedService(String serviceId, String name, double price) {
        usedServices.add(new UsedService(serviceId, name, price, new Date()));
    }

    public List<UsedService> getUsedServices() {
        return usedServices;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public void setRentRoom(Room room) {
        this.rentRoom = room;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (rentRoom != null && arriveDate != null) {
            return String.format("[" + id + "] " + fullName + " " + age + " лет, комната " + rentRoom.getNumber() + ", " + sdf.format(arriveDate) + "-" + sdf.format(departureDate));
        } else if (arriveDate == null) {
            return String.format("[" + id + "] " + fullName + " " + age + " лет, пока не заселен.");
        } else {
            return String.format("[" + id + "] " + fullName + " " + age + " лет, " + sdf.format(arriveDate) + "-" + sdf.format(departureDate));
        }
    }
}
