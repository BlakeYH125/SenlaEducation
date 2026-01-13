package model;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;

public class UsedService {
    private String usedServiceId;
    private String serviceId;
    private String guestId;
    private BigDecimal price;
    private Date date;

    public UsedService(String usedServiceId, String serviceId, String guestId, BigDecimal price, Date date) {
        this.usedServiceId = usedServiceId;
        this.serviceId = serviceId;
        this.guestId = guestId;
        this.price = price;
        this.date = date;
    }

    public UsedService() {}

    public String getUsedServiceId() {
        return usedServiceId;
    }

    public void setUsedServiceId(String usedServiceId) {
        this.usedServiceId = usedServiceId;
    }

    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    public String getId() {
        return serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDate(Date date) {
        this.date = date;
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
