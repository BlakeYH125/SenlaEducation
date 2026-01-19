package org.hotel.model;

import java.math.BigDecimal;

public final class Service implements Priceable {
    /**
     * Уникальный ID услуги.
     */
    private String id;

    /**
     * Название услуги.
     */
    private String name;

    /**
     * Цена услуги.
     */
    private BigDecimal price;

    /**
     * Раздел услуги.
     */
    private ServiceSection serviceSection;

    public Service(final String idP, final String nameP, final BigDecimal priceP, final ServiceSection serviceSectionP) {
        this.id = idP;
        this.name = nameP;
        this.price = priceP;
        this.serviceSection = serviceSectionP;
    }

    public Service() { }

    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public ServiceSection getServiceSection() {
        return serviceSection;
    }

    public void setName(final String nameP) {
        this.name = nameP;
    }

    public void setPrice(final BigDecimal priceP) {
        this.price = priceP;
    }

    public String getId() {
        return id;
    }

    public void setId(final String idP) {
        this.id = idP;
    }

    public void setServiceSection(final ServiceSection serviceSectionP) {
        this.serviceSection = serviceSectionP;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + ", стоимость: " + price;
    }
}
