package controller;

import model.*;
import view.Console;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RoomController {
    private Console console;
    private boolean running = true;
    private Administrator administrator;
    private RoomManagement roomManagement;

    public RoomController(Administrator administrator, Console console) {
        this.console = console;
        this.administrator = administrator;
        this.roomManagement = administrator.getRoomManagement();
    }

    public void run() {
        running = true;
        while (running) {
            console.printRoomMenu();
            int command = console.readInt("Введите номер команды: ");
            switch (command) {
                case 0:
                    running = false;
                    break;

                case 1:
                    addRoom();
                    break;

                case 2:
                    changeRoomPrice();
                    break;

                case 3:
                    showAllRooms();
                    break;

                case 4:
                    showAllFreeRooms();
                    break;

                case 5:
                    getFreeRoomsCount();
                    break;

                case 6:
                    showFreeRoomsByDate();
                    break;

                case 7:
                    getRoomDetails();
                    break;

                case 8:
                    settle();
                    break;

                case 9:
                    evict();
                    break;

                case 10:
                    getThreePrevGuests();
                    break;

                case 11:
                    setAvailable();
                    break;

                case 12:
                    setOccupied();
                    break;

                case 13:
                    setInService();
                    break;

                case 14:
                    importRoomData();
                    break;

                case 15:
                    exportRoomData();
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }

    public void settle() {
        try {
            GuestManagement guestManagement = administrator.getGuestManagement();
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            if (!roomManagement.isFree(id)) {
                console.showMessage("Комната занята, либо находится на обслуживании.");
                return;
            }
            int guestsCount = console.readInt("Введите количестве гостей: ");
            if (roomManagement.getRoom(id).getCapacity() < guestsCount) {
                console.showMessage("Комната рассчитана на меньшее количество человек.");
                return;
            }
            List<Guest> guests = new ArrayList<>();
            for (int i = 0; i < guestsCount; i++) {
                String guestId = console.readString("Введите id гостя: ");
                if (guestManagement.getGuests().containsKey(guestId)) {
                    Guest temp = guestManagement.getGuest(guestId);
                    guestManagement.removeGuest(id);
                    guests.add(temp);
                } else {
                    guests.add(new Guest(guestId, console.readString("Введите полное имя: "), console.readInt("Введите возраст: ")));
                }
            }
            int daysCount = console.readInt("Введите количество дней проживания: ");
            if (administrator.settle(id, guests, daysCount) == 0) {
                console.showMessage("Заселение успешно.");
            } else if (administrator.settle(id, guests, daysCount) == -1) {
                console.showMessage("Комната на обслуживании.");
            } else {
                console.showMessage("Комната занята.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void evict() {
        try {
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.isFree(id)) {
                console.showMessage("Комната никем не занята.");
                return;
            }
            if (administrator.evict(id)) {
                console.showMessage("Выселение успешно.");
            } else {
                console.showMessage("Ошибка при выселении.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void setAvailable() {
        String id = console.readString("Введите id комнаты: ");
        try {
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.getRoom(id).getGuests() != null && !roomManagement.getRoom(id).getGuests().isEmpty()) {
                console.showMessage("Сначала нужно выселить гостя.");
            } else if (roomManagement.isFree(id)) {
                console.showMessage("Номер уже свободен.");
            } else {
                if (roomManagement.setAvailable(id)) {
                    console.showMessage("Установка статуса успешна.");
                } else {
                    console.showMessage("Включен запрет на смену статуса у номера.");
                }
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void setOccupied() {
        String id = console.readString("Введите id комнаты: ");
        try {
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.isServicing(id)) {
                console.showMessage("Сначала нужно закончить обслуживание номера.");
            } else if (roomManagement.isOccupied(id)) {
                console.showMessage("Комната уже занята.");
            } else {
                int daysCount = console.readInt("Введите, на сколько дней установить статус \"занят\": ");
                if (roomManagement.setOccupied(id, daysCount)) {
                    console.showMessage("Установка статуса успешна.");
                } else {
                    console.showMessage("Включен запрет на смену статуса у номера.");
                }
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void setInService() {
        String id = console.readString("Введите id комнаты: ");
        try {
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.getRoom(id).getGuests() != null && !roomManagement.getRoom(id).getGuests().isEmpty()) {
                console.showMessage("Сначала нужно выселить гостя.");
            } else if (roomManagement.isServicing(id)) {
                console.showMessage("Комната уже на обслуживании");
            } else {
                int daysCount = console.readInt("Введите, на сколько дней установить статус \"на обслуживании\": ");
                if (roomManagement.setInService(id, daysCount)) {
                    console.showMessage("Установка статуса успешна.");
                } else {
                    console.showMessage("Включен запрет на смену статуса у номера.");
                }
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void changeRoomPrice() {
        String id = console.readString("Введите id комнаты: ");
        try {
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            double price = console.readDouble("Введите новую стоимость номера: ");
            roomManagement.setNewRoomPrice(id, price);
            console.showMessage("Изменение успешно.");
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void addRoom() {
        String id = console.readString("Введите id комнаты: ");
        try {
            if (roomManagement.getRooms().containsKey(id)) {
                console.showMessage("Комната с таким id уже есть.");
                return;
            }
            int number = console.readInt("Введите номер комнаты: ");
            roomManagement.addNewRoom(new Room(id, number, console.readDouble("Введите суточную стоимость номера: "), Status.AVAILABLE, console.readInt("Введите вместимость номера: "), console.readInt("Введите количество звезд: ")));
            console.showMessage("Добавление успешно.");
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }

    public void showAllRooms() {
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

    public void showAllFreeRooms() {
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

    public void getFreeRoomsCount() {
        console.showMessage(String.valueOf(roomManagement.getFreeRoomsCount()));
    }

    public void showFreeRoomsByDate() {
        int daysCount = console.readInt("Введите количество дней от текущей даты: ");
        console.showRooms(roomManagement.getFreeRoomsByDate(new Date(System.currentTimeMillis() + daysCount * RoomManagement.getMSecInDay())));
    }



    public void getRoomDetails() {
        if (roomManagement.getRooms() == null) {
            console.showMessage("Список комнат пуст.");
            return;
        }
        String id = console.readString("Введите id комнаты: ");
        if (!roomManagement.getRooms().containsKey(id)) {
            console.showMessage("Комнаты с таким id нет.");
            return;
        }
        console.showMessage(roomManagement.getRoomDetails(id));
    }

    public void importRoomData() {
        String filePath = console.readString("Введите абсолютный путь к файлу: ");
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
            String str;
            while ((str = br.readLine()) != null) {
                String[] parts = str.split(";");
                if (parts.length == 6) {
                    roomManagement.addNewRoom(new Room(parts[0], Integer.parseInt(parts[1]), Double.parseDouble(parts[2]), Status.valueOf(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));
                } else {
                    console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                }
            }
            console.showMessage("Импорт завершен.");
        } catch (NoSuchFileException e) {
            console.showMessage("Файл не найден.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportRoomData() {
        String id = console.readString("Введите id комнаты для экспорта: ");
        if (roomManagement.getRooms().containsKey(id)) {
            String dirPath = console.readString("Введите абсолютный путь к папке для экспорта: ");
            File directory = new File(dirPath);
            directory.mkdirs();
            File file = new File(directory, "room_export.csv");
            try {
                file.createNewFile();
                Room room = roomManagement.getRoom(id);
                String[] data = new String[6];
                data[0] = id;
                data[1] = String.valueOf(room.getNumber());
                data[2] = String.valueOf(room.getPrice());
                data[3] = room.getStatus().name();
                data[4] = String.valueOf(room.getCapacity());
                data[5] = String.valueOf(room.getStars());
                String result = String.join(";", data);
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(result);
                    bw.newLine();
                    console.showMessage("Экспорт успешен.");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            console.showMessage("Комнаты с таким id нет.");
        }
    }

    public void getThreePrevGuests() {
        RoomManagement roomManagement = administrator.getRoomManagement();
        String id = console.readString("Введите id комнаты: ");
        try {
            if (!roomManagement.getRooms().containsKey(id)) {
                throw new RoomNotFoundException();
            }
            console.showGuests((List<Guest>) roomManagement.getThreePrevRoomGuests(id));
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
        }
    }
}
