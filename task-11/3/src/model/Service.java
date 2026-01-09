package model;

import java.math.BigDecimal;

public class Service implements Priceable {
    private String id;
    private String name;
    private BigDecimal price;
    private ServiceSection serviceSection;

    public Service(String id, String name, BigDecimal price, ServiceSection serviceSection) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.serviceSection = serviceSection;
    }

    public Service() {}

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

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setServiceSection(ServiceSection serviceSection) {
        this.serviceSection = serviceSection;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + ", стоимость: " + price;
    }
}
