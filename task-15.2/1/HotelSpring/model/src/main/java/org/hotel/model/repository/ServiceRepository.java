package org.hotel.model.repository;

import org.hotel.model.entities.Service;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
public interface ServiceRepository {
    void save(Service service);

    List<Service> findAll();

    void setNewServicePrice(Service service, BigDecimal price);

    Service getService(String id);
}
