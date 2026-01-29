package org.hotel.model.management;

import org.hotel.model.entities.Guest;
import org.hotel.model.enums.SortType;
import org.hotel.model.entities.UsedService;
import org.hotel.model.repository.UsedServiceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public final class UsedServiceManagement {
    /**
     * Репозиторий для работы с использованными услугами в БД.
     */
    private UsedServiceRepository usedServiceRepository;

    public UsedServiceManagement(final UsedServiceRepository usedServiceRepositoryP) {
        this.usedServiceRepository = usedServiceRepositoryP;
    }

    public void addUsedService(final UsedService usedServiceP) {
        usedServiceRepository.save(usedServiceP);
    }

    public List<UsedService> getUsedServicesByGuestWithSort(final List<UsedService> usedServicesP, final SortType sortTypeP) {
        List<UsedService> sortedList = new ArrayList<>(usedServicesP);
        if (sortTypeP == SortType.PRICE) {
            sortedList.sort(Comparator.comparing(UsedService::getPrice));
        } else if (sortTypeP == SortType.DATE) {
            sortedList.sort(Comparator.comparing(UsedService::getDate));
        }
        return sortedList;
    }

    public List<UsedService> getUsedServices(final Guest guestP) {
        return usedServiceRepository.findServicesUsedByGuest(guestP);
    }
}
