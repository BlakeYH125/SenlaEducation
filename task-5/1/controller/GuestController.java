import education.task5.model.*;
import education.task5.view.Console;

import java.util.List;

public class GuestController {
    public void showGuests(Console console, GuestManagement guestManagement) {
        console.showMessage("1. Алфавит;\n2. Дата освобождения номера.");
        int sortType = console.readInt("Выберите вид сортировки: ");
        if (sortType == 1) {
            console.showGuests((List<Guest>) guestManagement.getGuestsWithSort(SortType.ALPHABET));
        } else if (sortType == 2) {
            console.showGuests((List<Guest>) guestManagement.getGuestsWithSort(SortType.DATE));
        } else {
            console.showMessage("Неверный номер команды.");
        }
    }

    public void getGuestsCount(Console console, GuestManagement guestManagement) {
        console.showMessage(String.valueOf(guestManagement.getGuestsCount()));
    }
}
