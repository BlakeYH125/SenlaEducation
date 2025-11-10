import education.task5.model.*;
import education.task5.view.Console;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomController {
    public void evict(Console console, Administrator administrator) {
        RoomManagement roomManagement = administrator.getRoomManagement();
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        if (!roomManagement.isFree(number)) {
            console.showMessage("Комната занята, либо находится на обслуживании.");
            return;
        }
        int guestsCount = console.readInt("Введите количестве гостей: ");
        if (roomManagement.getRoom(number).getCapacity() < guestsCount) {
            console.showMessage("Комната рассчитана на меньшее количество человек.");
            return;
        }
        List<Guest> guests = new ArrayList<>();
        for (int i = 0; i < guestsCount; i++) {
            guests.add(new Guest(console.readString("Введите полное имя: "), console.readInt("Введите возраст: ")));
        }
        int daysCount = console.readInt("Введите количество дней проживания: ");
        if (administrator.settle(number, guests, daysCount) == 0) {
            console.showMessage("Заселение успешно.");
        } else if (administrator.settle(number, guests, daysCount) == -1) {
            console.showMessage("Комната на обслуживании.");
        } else {
            console.showMessage("Комната занята.");
        }
    }

    public void settle(Console console, Administrator administrator) {
        RoomManagement roomManagement = administrator.getRoomManagement();
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        if (roomManagement.isFree(number)) {
            console.showMessage("Комната никем не занята.");
            return;
        }
        if (administrator.evict(number)) {
            console.showMessage("Выселение успешно.");
        } else {
            console.showMessage("Ошибка при выселении.");
        }
    }

    public void setAvailable(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        if (roomManagement.getRoom(number).getGuests() != null && !roomManagement.getRoom(number).getGuests().isEmpty()) {
            console.showMessage("Сначала нужно выселить гостя.");
        } else if (roomManagement.isFree(number)) {
            console.showMessage("Номер уже свободен.");
        } else {
            roomManagement.setAvailable(number);
            console.showMessage("Установка статуса успешна.");
        }
    }

    public void setOccupied(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        if (roomManagement.isServicing(number)) {
            console.showMessage("Сначала нужно закончить обслуживание номера.");
        } else if (roomManagement.isOccupied(number)) {
            console.showMessage("Комната уже занята.");
        } else {
            int daysCount = console.readInt("Введите, на сколько дней установить статус \"занят\": ");
            roomManagement.setOccupied(number, daysCount);
            console.showMessage("Установка статуса успешна.");
        }
    }

    public void setInService(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        if (roomManagement.getRoom(number).getGuests() != null && !roomManagement.getRoom(number).getGuests().isEmpty()) {
            console.showMessage("Сначала нужно выселить гостя.");
        } else if (roomManagement.isServicing(number)) {
            console.showMessage("Комната уже на обслуживании");
        } else {
            int daysCount = console.readInt("Введите, на сколько дней установить статус \"на обслуживании\": ");
            roomManagement.setInService(number, daysCount);
            console.showMessage("Установка статуса успешна.");
        }
    }

    public void changeRoomPrice(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        double price = console.readDouble("Введите новую стоимость номера: ");
        roomManagement.setNewRoomPrice(number, price);
        console.showMessage("Изменение успешно.");
    }

    public void addRoom(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комната с таким номером уже есть.");
            return;
        }
        roomManagement.addNewRoom(new Room(number, console.readDouble("Введите суточную стоимость номера: "), Status.AVAILABLE, console.readInt("Введите вместимость номера: "), console.readInt("Введите количество звезд: ")));
        console.showMessage("Добавление успешно.");
    }

    public void showAllRooms(Console console, RoomManagement roomManagement) {
        console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
        int sortType = console.readInt("Выберите вид сортировки: ");
        if (sortType == 1) {
            console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.PRICE));
        } else if (sortType == 2) {
            console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.CAPACITY));
        } else if (sortType == 3) {
            console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.STARS));
        } else {
            console.showMessage("Неверный номер команды.");
        }
    }

    public void showAllFreeRooms(Console console, RoomManagement roomManagement) {
        console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
        int sortType = console.readInt("Выберите вид сортировки: ");
        if (sortType == 1) {
            console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.PRICE));
        } else if (sortType == 2) {
            console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.CAPACITY));
        } else if (sortType == 3) {
            console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.STARS));
        } else {
            console.showMessage("Неверный номер команды.");
        }
    }

    public void getFreeRoomsCount(Console console, RoomManagement roomManagement) {
        console.showMessage(String.valueOf(roomManagement.getFreeRoomsCount()));
    }

    public void showFreeRoomsByDate(Console console, RoomManagement roomManagement) {
        int daysCount = console.readInt("Введите количество дней от текущей даты: ");
        console.showRooms(roomManagement.getFreeRoomsByDate(new Date(System.currentTimeMillis() + daysCount * RoomManagement.getMSecInDay())));
    }

    public void getTotalCost(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        if (roomManagement.isOccupied(number) && roomManagement.getRoom(number).getGuests() != null && !roomManagement.getRoom(number).getGuests().isEmpty()) {
            console.showMessage(String.valueOf(roomManagement.getTotalRoomCost(number)));
        } else {
            console.showMessage("В номере никто не живет.");
        }
    }

    public void getThreePrevGuests(Console console, RoomManagement roomManagement) {
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        console.showGuests((List<Guest>) roomManagement.getThreePrevRoomGuests(number));
    }

    public void getRoomDetails(Console console, RoomManagement roomManagement) {
        if (roomManagement.getRooms() == null) {
            console.showMessage("Список комнат пуст.");
            return;
        }
        int number = console.readInt("Введите номер комнаты: ");
        if (!roomManagement.getRooms().containsKey(number)) {
            console.showMessage("Комнаты с таким номером нет.");
            return;
        }
        console.showMessage(roomManagement.getRoomDetails(number));
    }
}
