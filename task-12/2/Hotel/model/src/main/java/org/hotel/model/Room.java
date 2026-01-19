package org.hotel.model;

import org.hotel.configurator.Configurator;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public final class Room implements Priceable {
    /**
     * Уникальный ID комнаты.
     */
    private String id;

    /**
     * Номер комнаты.
     */
    private int number;

    /**
     * Цена за комнату.
     */
    private BigDecimal price;

    /**
     * Вместимость комнаты.
     */
    private int capacity;

    /**
     * Количество звезд у комнаты.
     */
    private int stars;

    /**
     * Статус комнаты.
     */
    private Status status;

    /**
     * Дата освобождения комнаты.
     */
    private Date releasedIn;

    public Room(final String idP, final int numberP, final BigDecimal priceP, final Status statusP, final int capacityP, final int starsP) {
        this.id = id;
        this.number = numberP;
        this.price = priceP;
        this.capacity = capacityP;
        this.stars = starsP;
        this.status = statusP;
        Configurator.configure(this);
    }

    public Room() { }

    public int getNumber() {
        return number;
    }

    public void setNumber(final int numberP) {
        this.number = numberP;
    }

    public void setCapacity(final int capacityP) {
        this.capacity = capacityP;
    }

    public void setStars(final int starsP) {
        this.stars = starsP;
    }

    public String getId() {
        return id;
    }

    public void setId(final String idP) {
        this.id = idP;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(final BigDecimal priceP) {
        this.price = priceP;
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

    public void setStatus(final Status statusP) {
        this.status = statusP;
    }

    public Date getReleasedIn() {
        return releasedIn;
    }

    public void setReleasedIn(final Date releasedInP) {
        this.releasedIn = releasedInP;
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
