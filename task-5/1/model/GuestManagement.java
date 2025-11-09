import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuestManagement {
    private List<Guest> guests;

    public GuestManagement() {
        this.guests = new ArrayList<>();
    }

    public int getGuestsCount() {
        return guests.size();
    }

    public void addGuest(Guest guest) {
        guests.add(guest);
    }

    public void removeGuest(Guest guest) {
        guests.remove(guest);
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public List<Guest> getGuestsWithSort(SortType sortType) {
        List<Guest> listGuests = new ArrayList<>(guests);
        if (sortType == SortType.ALPHABET) {
            listGuests.sort(Comparator.comparing(Guest::getFullName));
        } else if (sortType == SortType.DATE) {
            listGuests.sort(Comparator.comparing(Guest::getDepartureDate));
        }
        return listGuests;
    }
}
