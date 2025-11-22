package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;


public class GuestManagement implements Serializable {
    private Map<String, Guest> guests;

    public GuestManagement() {
        this.guests = new HashMap<>();
    }

    public int getGuestsCount() {
        return guests.size();
    }

    public void addGuest(Guest guest) {
        guests.put(guest.getId(), guest);
    }

    public Guest getGuest(String id) {
        return guests.get(id);
    }

    public void removeGuest(String id) {
        guests.remove(id);
    }

    public Map<String, Guest> getGuests() {
        return guests;
    }

    public List<Guest> getGuestsWithSort(SortType sortType) {
        List<Guest> listGuests = new ArrayList<>(guests.values());
        if (sortType == SortType.ALPHABET) {
            listGuests.sort(Comparator.comparing(Guest::getFullName));
        } else if (sortType == SortType.DATE) {
            listGuests.sort(Comparator.comparing(Guest::getDepartureDate));
        }
        return listGuests;
    }

    public List<UsedService> getUsedServicesByGuestWithSort(List<UsedService> usedServices, SortType sortType) {
        if (sortType == SortType.PRICE) {
            usedServices.sort(Comparator.comparing(UsedService::getPrice));
        } else if (sortType == SortType.DATE) {
            usedServices.sort(Comparator.comparing(UsedService::getDate));
        }
        return usedServices;
    }
}
