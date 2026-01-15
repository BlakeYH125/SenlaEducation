package org.hotel.model;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;

import java.util.*;

@Component
public class UsedServiceManagement {

    @Inject
    UsedServiceRepository usedServiceRepository;

    public UsedServiceManagement() {
    }

    public void addUsedService(UsedService usedService) {
        usedServiceRepository.save(usedService);
    }

    public List<UsedService> getUsedServicesByGuestWithSort(List<UsedService> usedServices, SortType sortType) {
        List<UsedService> sortedList = new ArrayList<>(usedServices);
        if (sortType == SortType.PRICE) {
            sortedList.sort(Comparator.comparing(UsedService::getPrice));
        } else if (sortType == SortType.DATE) {
            sortedList.sort(Comparator.comparing(UsedService::getDate));
        }
        return sortedList;
    }

    public List<UsedService> getUsedServices(Guest guest) {
        return usedServiceRepository.findServicesUsedByGuest(guest);
    }
}
