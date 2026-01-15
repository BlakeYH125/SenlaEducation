package org.hotel.model;

import org.hotel.annotations.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public interface ServiceRepository {
    void save(Service service);

    List<Service> findAll();

    void setNewServicePrice(Service service, BigDecimal price);

    Service getService(String id);
}
