package model;

import annotations.Component;
import annotations.Inject;
import dao.UsedServiceDao;

import java.util.*;

@Component
public class UsedServiceManagement {

    @Inject
    UsedServiceDao usedServiceDao;

    public UsedServiceManagement() {
    }

    public void addUsedService(UsedService usedService) {
        usedServiceDao.save(usedService);
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
        return usedServiceDao.findServicesUsedByGuest(guest);
    }
}
