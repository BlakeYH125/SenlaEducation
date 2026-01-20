package org.hotel.model;

import java.util.Date;
import java.text.SimpleDateFormat;

public final class Guest {
    /**
     * Уникальный id клиента.
     */
    private String id;

    /**
     * Полное имя.
     */
    private String fullName;

    /**
     * Возраст.
     */
    private int age;

    /**
     * ID арендуемой комнаты.
     */
    private String rentRoomId;

    /**
     * Дата заезда.
     */
    private Date arriveDate;

    /**
     * Дата выезда.
     */
    private Date departureDate;

    /**
     * Статус гостя.
     */
    private GuestStatus status;

    public Guest(final String paramId, final String paramFullName, final int paramAge) {
        this.id = paramId;
        this.fullName = paramFullName;
        this.age = paramAge;
        this.status = GuestStatus.SETTLED;
    }

    public Guest() { }

    public void setFullName(final String paramFullName) {
        this.fullName = paramFullName;
    }

    public void setAge(final int paramAge) {
        this.age = paramAge;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(final GuestStatus paramStatus) {
        this.status = paramStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public void setId(final String paramId) {
        this.id = paramId;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(final Date paramArriveDate) {
        this.arriveDate = paramArriveDate;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(final Date paramDepartureDate) {
        this.departureDate = paramDepartureDate;
    }

    public void setRentRoomId(final String paramRentRoomId) {
        this.rentRoomId = paramRentRoomId;
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
