package org.hotel.model;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public final class UsedService {
    /**
     * Уникальный ID использованной услуги.
     */
    private String usedServiceId;

    /**
     * Уникальный ID услуги.
     */
    private String serviceId;

    /**
     * Уникальный ID гостя.
     */
    private String guestId;

    /**
     * Стоимость использованной услуги.
     */
    private BigDecimal price;

    /**
     * Дата использования услуги.
     */
    private Date date;

    public UsedService(final String usedServiceIdP, final String serviceIdP, final String guestIdP, final BigDecimal priceP, final Date dateP) {
        this.usedServiceId = usedServiceIdP;
        this.serviceId = serviceIdP;
        this.guestId = guestIdP;
        this.price = priceP;
        this.date = dateP;
    }

    public UsedService() { }

    public String getUsedServiceId() {
        return usedServiceId;
    }

    public void setUsedServiceId(final String usedServiceIdP) {
        this.usedServiceId = usedServiceIdP;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(final String guestIdP) {
        this.guestId = guestIdP;
    }

    public String getId() {
        return serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(final String serviceIdP) {
        this.serviceId = serviceIdP;
    }

    public void setPrice(final BigDecimal priceP) {
        this.price = priceP;
    }

    public void setDate(final Date dateP) {
        this.date = dateP;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return "Услуга [" + getId() + "], " + "стоимость " + getPrice() + ", использована " + sdf.format(date);
    }
}
