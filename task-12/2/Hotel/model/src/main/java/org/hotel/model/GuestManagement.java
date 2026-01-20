package org.hotel.model;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;

import java.util.Comparator;
import java.util.List;

@Component
public final class GuestManagement {

    /**
     * Репозиторий для работы с гостями в БД.
     */
    @Inject
    private GuestRepository guestRepository;

    public GuestManagement() {
    }

    public int getGuestsCount() {
        return guestRepository.findCurrentGuestsInHotel().size();
    }

    public void addGuest(final Guest guest) {
        guestRepository.save(guest);
    }

    public Guest getGuest(final String id) {
        return guestRepository.getGuest(id);
    }

    public void setEvicted(final String id) {
        guestRepository.setEvicted(getGuest(id));
    }

    public List<Guest> getActualGuests() {
        return guestRepository.findCurrentGuestsInHotel();
    }

    public void setGuests(final List<Guest> guests) {
        for (Guest guest : guests) {
            guestRepository.save(guest);
        }
    }

    public boolean isThereGuest(final String id) {
        if (guestRepository.getGuest(id) == null) {
            return false;
        }
        return true;
    }

    public List<Guest> getActualGuestsWithSort(final SortType sortType) {
        List<Guest> listGuests = guestRepository.findCurrentGuestsInHotel();
        if (sortType == SortType.ALPHABET) {
            listGuests.sort(Comparator.comparing(Guest::getFullName));
        } else if (sortType == SortType.DATE) {
            listGuests.sort(Comparator.comparing(Guest::getDepartureDate));
        }
        return listGuests;
    }
}
