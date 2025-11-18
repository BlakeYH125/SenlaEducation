package model;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Guest extends Person {
    private String id;
    private Room rentRoom;
    private Date arriveDate;
    private Date departureDate;

    public Guest(String id, String fullName, int age) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
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
