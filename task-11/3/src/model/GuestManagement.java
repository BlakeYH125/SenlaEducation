package model;

import annotations.Component;
import annotations.Inject;
import dao.GuestDao;

import java.util.Comparator;
import java.util.List;

@Component
public class GuestManagement {

    @Inject
    GuestDao guestDao;

    public GuestManagement() {
    }

    public int getGuestsCount() {
        return guestDao.findCurrentGuestsInHotel().size();
    }

    public void addGuest(Guest guest) {
        guestDao.save(guest);
    }

    public Guest getGuest(String id) {
        return guestDao.getGuest(id);
    }

    public void setEvicted(String id) {
        guestDao.setEvicted(getGuest(id));
    }

    public List<Guest> getActualGuests() {
        return guestDao.findCurrentGuestsInHotel();
    }

    public void setGuests(List<Guest> guests) {
        for (Guest guest : guests) {
            guestDao.save(guest);
        }
    }

    public boolean isThereGuest(String id) {
        if (guestDao.getGuest(id) == null) {
            return false;
        }
        return true;
    }

    public List<Guest> getActualGuestsWithSort(SortType sortType) {
        List<Guest> listGuests = guestDao.findCurrentGuestsInHotel();
        if (sortType == SortType.ALPHABET) {
            listGuests.sort(Comparator.comparing(Guest::getFullName));
        } else if (sortType == SortType.DATE) {
            listGuests.sort(Comparator.comparing(Guest::getDepartureDate));
        }
        return listGuests;
    }
}
