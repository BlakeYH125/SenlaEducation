package model;

import annotations.Component;
import annotations.Inject;
import annotations.PostConstruct;
import dao.GuestDao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Component
public class GuestManagement {
    @Inject
    GuestDao guestDao;

    private Map<String, Guest> guests;

    public GuestManagement() {
        this.guests = new HashMap<>();
    }

    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        guests.clear();
        List<Guest> guests = guestDao.findAll();
        for (Guest guest : guests) {
            this.guests.put(guest.getId(), guest);
        }
    }

    public int getGuestsCount() {
        int counter = 0;
        for (Guest guest : guests.values()) {
            if (guest.getStatus().equals(GuestStatus.SETTLED)) {
                counter ++;
            }
        }
        return counter;
    }

    public void addGuest(Guest guest) {
        guestDao.save(guest);
        guests.put(guest.getId(), guest);
    }

    public Guest getGuest(String id) {
        return guests.get(id);
    }

    public void removeGuest(String id) {
        guests.get(id).setStatus(GuestStatus.EVICTED);
        guestDao.save(getGuest(id));
    }

    public Map<String, Guest> getGuests() {
        return new HashMap<>(guests);
    }

    public void setGuests(List<Guest> guests) {
        for (Guest guest : guests) {
            guestDao.save(guest);
        }
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
}
