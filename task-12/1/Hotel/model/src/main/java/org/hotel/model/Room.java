package org.hotel.model;

import org.hotel.configurator.Configurator;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Room implements Priceable {
    private String id;
    private int number;
    private BigDecimal price;
    private int capacity;
    private int stars;
    private Status status;
    private Date releasedIn;

    public Room(String id, int number, BigDecimal price, Status status, int capacity, int stars) {
        this.id = id;
        this.number = number;
        this.price = price;
        this.capacity = capacity;
        this.stars = stars;
        this.status = status;
        Configurator.configure(this);
    }

    public Room() {}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        if (releasedIn == null) {
            return "[" + id  + "] номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                    + stars + ", " + status.toString();

        }
        return "[" + id  + "] номер " + number + ", стоимость: " + price + ", вместимость: " + capacity + ", звезды: "
                + stars + ", " + status.toString() + ", освободится " + sdf.format(releasedIn);
    }
}
