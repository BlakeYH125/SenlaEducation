package model;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Guest {
    private String id;
    private String fullName;
    private int age;
    private String rentRoomId;
    private Date arriveDate;
    private Date departureDate;
    private GuestStatus status;

    public Guest(String id, String fullName, int age) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
        this.status = GuestStatus.SETTLED;
    }

    public Guest() {}

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(GuestStatus status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setRentRoomId(String rentRoomId) {
        this.rentRoomId = rentRoomId;
    }

    public String getId() {
        return id;
    }

    public String getRentRoomId() {
        return rentRoomId;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (rentRoomId != null && arriveDate != null) {
            return String.format("[" + id + "] " + fullName + " " + age + " лет, комната " + rentRoomId + ", " + sdf.format(arriveDate) + "-" + sdf.format(departureDate) + ", " + status);
        } else if (arriveDate == null) {
            return String.format("[" + id + "] " + fullName + " " + age + " лет, пока не заселен.");
        } else {
            return String.format("[" + id + "] " + fullName + " " + age + " лет, " + sdf.format(arriveDate) + "-" + sdf.format(departureDate) + ", " + status);
        }
    }
}
