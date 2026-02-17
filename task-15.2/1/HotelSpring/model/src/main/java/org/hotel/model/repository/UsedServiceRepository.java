package org.hotel.model.repository;


import org.hotel.model.entities.Guest;
import org.hotel.model.entities.UsedService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsedServiceRepository {
    void save(UsedService usedService);
    List<UsedService> findServicesUsedByGuest(Guest guest);
}
