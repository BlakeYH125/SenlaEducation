package model;

import annotations.ConfigProperty;
import configurator.Configurator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class Room implements Priceable, Serializable {
    private String id;
    private int number;
    private double price;
    private int capacity;
    private int stars;
    private Status status;
    private List<Guest> guests;
    private Date releasedIn;
    private List<Guest> previousGuests;
    @ConfigProperty(propertyName = "hotel.room.history.limit")
    private int previousGuestsLimit;

    public Room(String id, int number, double price, Status status, int capacity, int stars) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.capacity = capacity;
        this.stars = stars;
        this.status = status;
        previousGuests = new ArrayList<>();
        Configurator.configure(this);
    }

    public int getNumber() {
        return number;
    }

    public String getId() {
        return id;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    @Override
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStars() {
        return stars;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getReleasedIn() {
        return releasedIn;
    }

    public void setReleasedIn(Date releasedIn) {
        this.releasedIn = releasedIn;
    }

    public void addToPrevGuestsList() {
        for (Guest guest : guests) {
            if (!previousGuests.isEmpty() && previousGuests.size() == previousGuestsLimit) {
                previousGuests.remove(0);
            }
            previousGuests.add(guest);
        }
        guests = null;
    }

    public List<Guest> getPrevGuests() {
        return previousGuests;
    }

    public

    @Override
    String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        if (releasedIn == null) {
            return "[" + id  + "] номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                    + stars + ", " + status.toString();

        }
        return "[" + id  + "] номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                + stars + ", " + status.toString() + ", освободится " + sdf.format(releasedIn);
    }
}
