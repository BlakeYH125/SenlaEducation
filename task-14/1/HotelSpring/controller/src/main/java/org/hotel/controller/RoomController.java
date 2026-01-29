package org.hotel.controller;

import org.hotel.constants.CommandConstants;
import org.hotel.constants.ParametersConstants;
import org.hotel.constants.StatusConstants;
import org.hotel.model.management.Administrator;
import org.hotel.model.entities.Room;
import org.hotel.model.management.RoomManagement;
import org.hotel.model.exceptions.RoomNotFoundException;
import org.hotel.model.entities.Guest;
import org.hotel.model.management.GuestManagement;
import org.hotel.model.enums.SortType;
import org.hotel.model.enums.Status;
import org.hotel.model.exceptions.WrongCommandNumberException;
import org.hotel.view.Console;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public final class RoomController {
    /**
     * Логгер для фиксации логов.
     */
    private static final Logger LOGGER = LogManager.getLogger(GuestController.class);

    /**
     * Администратор.
     */
    private final Administrator administrator;

    /**
     * Вывод на консоль.
     */
    private final Console console;

    /**
     * Индикатор работы.
     */
    private boolean running = true;

    /**
     * Управление комнатами.
     */
    private RoomManagement roomManagement;

    public RoomController(final Administrator administratorP, final Console consoleP) {
        this.administrator = administratorP;
        this.console = consoleP;
    }

    public void init() {
        this.roomManagement = administrator.getRoomManagement();
    }

    public void run() {
        running = true;
        while (running) {
            console.printRoomMenu();
            int command = console.readInt("Введите номер команды: ");
            switch (command) {
                case CommandConstants.COMMAND_ZERO:
                    running = false;
                    break;

                case CommandConstants.COMMAND_ONE:
                    addRoom();
                    break;

                case CommandConstants.COMMAND_TWO:
                    changeRoomPrice();
                    break;

                case CommandConstants.COMMAND_THREE:
                    showAllRooms();
                    break;

                case CommandConstants.COMMAND_FOUR:
                    showAllFreeRooms();
                    break;

                case CommandConstants.COMMAND_FIVE:
                    getFreeRoomsCount();
                    break;

                case CommandConstants.COMMAND_SIX:
                    showFreeRoomsByDate();
                    break;

                case CommandConstants.COMMAND_SEVEN:
                    getRoomDetails();
                    break;

                case CommandConstants.COMMAND_EIGHT:
                    settle();
                    break;

                case CommandConstants.COMMAND_NINE:
                    evict();
                    break;

                case CommandConstants.COMMAND_TEN:
                    getThreePrevGuests();
                    break;

                case CommandConstants.COMMAND_ELEVEN:
                    setAvailable();
                    break;

                case CommandConstants.COMMAND_TWELVE:
                    setOccupied();
                    break;

                case CommandConstants.COMMAND_THIRTEEN:
                    setInService();
                    break;

                case CommandConstants.COMMAND_FOURTEEN:
                    importRoomData();
                    break;

                case CommandConstants.COMMAND_FIFTEEN:
                    exportRoomData();
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }

    public void settle() {
        try {
            LOGGER.info("Начало выполнения метода settle");
            GuestManagement guestManagementP = administrator.getGuestManagement();
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            if (!roomManagement.isFree(id)) {
                console.showMessage("Комната занята, либо находится на обслуживании.");
                LOGGER.error("Ошибка при выполнении метода settle: Комната занята, либо находится на обслуживании.");
                return;
            }
            int guestsCount = console.readInt("Введите количестве гостей: ");
            if (roomManagement.getRoom(id).getCapacity() < guestsCount) {
                console.showMessage("Комната рассчитана на меньшее количество человек.");
                LOGGER.error("Ошибка при выполнении метода settle: Комната рассчитана на меньшее количество человек.");
                return;
            }
            List<Guest> guests = new ArrayList<>();
            for (int i = 0; i < guestsCount; i++) {
                String guestId = console.readString("Введите id гостя: ");
                if (guestManagementP.isThereGuest(guestId)) {
                    Guest temp = guestManagementP.getGuest(guestId);
                    guests.add(temp);
                } else {
                    Guest guest = new Guest(guestId, console.readString("Введите полное имя: "), console.readInt("Введите возраст: "));
                    guests.add(guest);
                }
            }
            int daysCount = console.readInt("Введите количество дней проживания: ");
            int status = administrator.settle(id, guests, daysCount);
            if (status == StatusConstants.SUCCESS_STATUS) {
                console.showMessage("Заселение успешно.");
                LOGGER.info("Метод settle успешно завершил работу");
            } else if (status == StatusConstants.IN_SERVICE_STATUS) {
                console.showMessage("Комната на обслуживании.");
                LOGGER.error("Ошибка при выполнении метода settle: Комната на обслуживании.");
            } else if (status == StatusConstants.OCCUPIED_STATUS) {
                console.showMessage("Комната занята.");
                LOGGER.error("Ошибка при выполнении метода settle: Комната занята.");
            } else {
                console.showMessage("Ошибка транзакции.");
                LOGGER.error("Ошибка при выполнении метода settle: Ошибка транзакции.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода settle: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода settle: " + e.getMessage());
        }
    }

    public void evict() {
        try {
            LOGGER.info("Начало выполнения метода evict");
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.isFree(id)) {
                console.showMessage("Комната никем не занята.");
                LOGGER.error("Ошибка при выполнении метода evict: Комната никем не занята.");
                return;
            }
            if (administrator.evict(id)) {
                console.showMessage("Выселение успешно.");
                LOGGER.info("Метод evict успешно завершил работу");
            } else {
                console.showMessage("Ошибка при выселении.");
                LOGGER.error("Ошибка при выполнении метода evict: Ошибка при выселении.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода evict: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода evict: " + e.getMessage());
        }
    }

    public void setAvailable() {
        try {
            LOGGER.info("Начало выполнения метода setAvailable");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                if (roomManagement.getCurrentGuests(roomManagement.getRoom(id)) != null && !roomManagement.getCurrentGuests(roomManagement.getRoom(id)).isEmpty()) {
                    console.showMessage("Сначала нужно выселить гостя.");
                    LOGGER.error("Ошибка при выполнении метода setAvailable: Сначала нужно выселить гостя.");
                } else if (roomManagement.isFree(id)) {
                    console.showMessage("Номер уже свободен.");
                    LOGGER.error("Ошибка при выполнении метода setAvailable: Номер уже свободен.");
                } else {
                    if (roomManagement.setAvailable(id)) {
                        console.showMessage("Установка статуса успешна.");
                        LOGGER.info("Метод setAvailable успешно завершил работу");
                    } else {
                        console.showMessage("Включен запрет на смену статуса у номера.");
                        LOGGER.error("Ошибка при выполнении метода setAvailable: Включен запрет на смену статуса у номера.");
                    }
                }
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                LOGGER.error("Ошибка при выполнении метода setAvailable: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода setAvailable: " + e.getMessage());
        }
    }

    public void setOccupied() {
        try {
            LOGGER.info("Начало выполнения метода setOccupied");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                if (roomManagement.isServicing(id)) {
                    console.showMessage("Сначала нужно закончить обслуживание номера.");
                    LOGGER.error("Ошибка при выполнении метода setOccupied: Сначала нужно закончить обслуживание номера.");
                } else if (roomManagement.isOccupied(id)) {
                    console.showMessage("Комната уже занята.");
                    LOGGER.error("Ошибка при выполнении метода setOccupied: Комната уже занята.");
                } else {
                    int daysCount = console.readInt("Введите, на сколько дней установить статус \"занят\": ");
                    if (roomManagement.setStatus(id, daysCount, Status.OCCUPIED)) {
                        console.showMessage("Установка статуса успешна.");
                        LOGGER.info("Метод setOccupied успешно завершил работу");
                    } else {
                        console.showMessage("Включен запрет на смену статуса у номера.");
                        LOGGER.error("Ошибка при выполнении метода setOccupied: Включен запрет на смену статуса у номера.");
                    }
                }
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                LOGGER.error("Ошибка при выполнении метода setOccupied: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода setOccupied: " + e.getMessage());
        }
    }

    public void setInService() {
        try {
            LOGGER.info("Начало выполнения метода setInService");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                if (roomManagement.getCurrentGuests(roomManagement.getRoom(id)) != null && !roomManagement.getCurrentGuests(roomManagement.getRoom(id)).isEmpty()) {
                    console.showMessage("Сначала нужно выселить гостя.");
                    LOGGER.error("Ошибка при выполнении метода setInService: Сначала нужно выселить гостя.");
                } else if (roomManagement.isServicing(id)) {
                    console.showMessage("Комната уже на обслуживании.");
                    LOGGER.error("Ошибка при выполнении метода setInService: Комната уже на обслуживании.");
                } else {
                    int daysCount = console.readInt("Введите, на сколько дней установить статус \"на обслуживании\": ");
                    if (roomManagement.setStatus(id, daysCount, Status.IN_SERVICE)) {
                        console.showMessage("Установка статуса успешна.");
                        LOGGER.info("Метод setInService успешно завершил работу");
                    } else {
                        console.showMessage("Включен запрет на смену статуса у номера.");
                        LOGGER.error("Ошибка при выполнении метода setInService: Включен запрет на смену статуса у номера.");
                    }
                }
                LOGGER.info("Метод setInService успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                LOGGER.error("Ошибка при выполнении метода setInService: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода setInService: " + e.getMessage());
        }
    }

    public void changeRoomPrice() {
        try {
            LOGGER.info("Начало выполнения метода changeRoomPrice");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                BigDecimal price = console.readBigDecimal("Введите новую стоимость номера: ");
                roomManagement.setNewRoomPrice(id, price);
                console.showMessage("Изменение успешно.");
                LOGGER.info("Метод changeRoomPrice успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                LOGGER.error("Ошибка при выполнении метода changeRoomPrice: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода changeRoomPrice: " + e.getMessage());
        }
    }

    public void addRoom() {
        try {
            LOGGER.info("Начало выполнения метода addRoom");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (roomManagement.isThereRoom(id)) {
                    console.showMessage("Комната с таким id уже есть.");
                    LOGGER.error("Ошибка при выполнении метода addRoom: Комната с таким id уже есть.");
                    return;
                }
                int number = console.readInt("Введите номер комнаты: ");
                roomManagement.addNewRoom(new Room(id, number, console.readBigDecimal("Введите суточную стоимость номера: "), Status.AVAILABLE, console.readInt("Введите вместимость номера: "), console.readInt("Введите количество звезд: ")));
                console.showMessage("Добавление успешно.");
                LOGGER.info("Метод addRoom успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                LOGGER.error("Ошибка при выполнении метода getRoomDetailsaddRoom: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода addRoom: " + e.getMessage());
        }
    }

    public void showAllRooms() {
        try {
            LOGGER.info("Начало выполнения метода showAllRooms");
            console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == CommandConstants.COMMAND_ONE) {
                console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.PRICE));
            } else if (sortType == CommandConstants.COMMAND_TWO) {
                console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.CAPACITY));
            } else if (sortType == CommandConstants.COMMAND_THREE) {
                console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.STARS));
            } else {
                throw new WrongCommandNumberException();
            }
            LOGGER.info("Метод showAllRooms успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода showAllRooms: " + e.getMessage());
        } catch
        (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showAllRooms: " + e.getMessage());
        }
    }

    public void showAllFreeRooms() {
        try {
            LOGGER.info("Начало выполнения метода showAllFreeRooms");
            console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == CommandConstants.COMMAND_ONE) {
                console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.PRICE));
            } else if (sortType == CommandConstants.COMMAND_TWO) {
                console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.CAPACITY));
            } else if (sortType == CommandConstants.COMMAND_THREE) {
                console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.STARS));
            } else {
                throw new WrongCommandNumberException();
            }
            LOGGER.info("Метод showAllFreeRooms успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода showAllFreeRooms: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showAllFreeRooms: " + e.getMessage());
        }
    }

    public void getFreeRoomsCount() {
        try {
            LOGGER.info("Начало выполнения метода getFreeRoomsCount");
            console.showMessage(String.valueOf(roomManagement.getFreeRoomsCount()));
            LOGGER.info("Метод getFreeRoomsCount успешно завершил работу");
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода getFreeRoomsCount: " + e.getMessage());
        }
    }

    public void showFreeRoomsByDate() {
        try {
            LOGGER.info("Начало выполнения метода showFreeRoomsByDate");
            int daysCount = console.readInt("Введите количество дней от текущей даты: ");
            console.showRooms(roomManagement.getFreeRoomsByDate(new Date(System.currentTimeMillis() + daysCount * RoomManagement.getMSecInDay())));
            LOGGER.info("Метод showFreeRoomsByDate успешно завершил работу");
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showFreeRoomsByDate: " + e.getMessage());
        }
    }

    public void getRoomDetails() {
        try {
            LOGGER.info("Начало выполнения метода getRoomDetails");
            if (roomManagement.getRooms() == null) {
                console.showMessage("Список комнат пуст.");
                LOGGER.error("Ошибка при выполнении метода getRoomDetails: Список комнат пуст.");
                return;
            }
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            console.showMessage(roomManagement.getRoomDetails(id));
            LOGGER.info("Метод getRoomDetails успешно завершил работу");
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода getRoomDetails: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода getRoomDetails: " + e.getMessage());
        }
    }

    public void importRoomData() {
        try {
            LOGGER.info("Начало выполнения метода importRoomData");
            String filePath = console.readString("Введите абсолютный путь к файлу: ");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
                String str;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(";");
                    if (parts.length == ParametersConstants.ROOM_PARAMETERS_COUNT) {
                        roomManagement.addNewRoom(new Room(parts[CommandConstants.COMMAND_ZERO], Integer.parseInt(parts[CommandConstants.COMMAND_ONE]), new BigDecimal(parts[CommandConstants.COMMAND_TWO]), Status.valueOf(parts[CommandConstants.COMMAND_THREE]), Integer.parseInt(parts[CommandConstants.COMMAND_FOUR]), Integer.parseInt(parts[CommandConstants.COMMAND_FIVE])));
                        console.showMessage("Импорт завершен.");
                    } else {
                        console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                        LOGGER.error("Ошибка при выполнении метода importRoomData: Ошибка при импорте, неверное количество параметров в записи.");
                    }
                }
                LOGGER.info("Метод importRoomData успешно завершил работу");
            } catch (NoSuchFileException e) {
                console.showMessage("Файл не найден.");
                LOGGER.error("Ошибка при выполнении метода importRoomData: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Ошибка при выполнении метода importRoomData: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода importRoomData: " + e.getMessage());
        }
    }

    public void exportRoomData() {
        try {
            LOGGER.info("Начало выполнения метода exportRoomData");
            String id = console.readString("Введите id комнаты для экспорта: ");
            if (!roomManagement.isThereRoom(id)) {
                String dirPath = console.readString("Введите абсолютный путь к папке для экспорта: ");
                File directory = new File(dirPath);
                directory.mkdirs();
                File file = new File(directory, "room_export.csv");
                try {
                    file.createNewFile();
                    Room room = roomManagement.getRoom(id);
                    String[] data = new String[ParametersConstants.ROOM_PARAMETERS_COUNT];
                    data[CommandConstants.COMMAND_ZERO] = id;
                    data[CommandConstants.COMMAND_ONE] = String.valueOf(room.getNumber());
                    data[CommandConstants.COMMAND_TWO] = String.valueOf(room.getPrice());
                    data[CommandConstants.COMMAND_THREE] = room.getStatus().name();
                    data[CommandConstants.COMMAND_FOURTEEN] = String.valueOf(room.getCapacity());
                    data[CommandConstants.COMMAND_FIVE] = String.valueOf(room.getStars());
                    String result = String.join(";", data);
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                        bw.write(result);
                        bw.newLine();
                        console.showMessage("Экспорт успешен.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOGGER.error("Ошибка при выполнении метода exportRoomData: " + e.getMessage());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RoomNotFoundException();
            }
            LOGGER.info("Метод exportRoomData успешно завершил работу");
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода getRoomDetails: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода exportRoomData: " + e.getMessage());
        }
    }

    public void getThreePrevGuests() {
        try {
            LOGGER.info("Начало выполнения метода getThreePrevGuests");
            RoomManagement roomManagementP = administrator.getRoomManagement();
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagementP.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                console.showGuests((List<Guest>) roomManagementP.getThreePrevRoomGuests(id));
                LOGGER.info("Метод getThreePrevGuests успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                LOGGER.error("Ошибка при выполнении метода getThreePrevGuests: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода getThreePrevGuests: " + e.getMessage());
        }
    }
}
