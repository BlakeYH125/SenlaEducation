package org.hotel.model;

import org.hotel.annotations.Component;

import java.util.List;

@Component
public interface UsedServiceRepository {
    void save(UsedService usedService);
    List<UsedService> findServicesUsedByGuest(Guest guest);
}
