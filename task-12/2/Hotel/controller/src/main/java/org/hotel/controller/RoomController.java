package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.dao.GuestDao;
import org.hotel.dao.RoomDao;
import org.hotel.model.*;
import org.hotel.view.Console;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

@Component
public class RoomController {
    private static final Logger logger = LogManager.getLogger(RoomController.class);

    @Inject
    private Administrator administrator;

    @Inject
    private Console console;

    @Inject
    private RoomDao roomDao;

    @Inject
    private GuestDao guestDao;

    private boolean running = true;
    private RoomManagement roomManagement;

    public RoomController() {
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
            logger.info("Начало выполнения метода settle");
            GuestManagement guestManagement = administrator.getGuestManagement();
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            if (!roomManagement.isFree(id)) {
                console.showMessage("Комната занята, либо находится на обслуживании.");
                logger.error("Ошибка при выполнении метода settle: Комната занята, либо находится на обслуживании.");
                return;
            }
            int guestsCount = console.readInt("Введите количестве гостей: ");
            if (roomManagement.getRoom(id).getCapacity() < guestsCount) {
                console.showMessage("Комната рассчитана на меньшее количество человек.");
                logger.error("Ошибка при выполнении метода settle: Комната рассчитана на меньшее количество человек.");
                return;
            }
            List<Guest> guests = new ArrayList<>();
            for (int i = 0; i < guestsCount; i++) {
                String guestId = console.readString("Введите id гостя: ");
                if (guestManagement.isThereGuest(guestId)) {
                    Guest temp = guestManagement.getGuest(guestId);
                    guests.add(temp);
                } else {
                    Guest guest = new Guest(guestId, console.readString("Введите полное имя: "), console.readInt("Введите возраст: "));
                    guests.add(guest);
                }
            }
            int daysCount = console.readInt("Введите количество дней проживания: ");
            int status = administrator.settle(id, guests, daysCount);
            if (status == 0) {
                for (Guest guest : guests) {
                    guestDao.save(guest);
                }
                console.showMessage("Заселение успешно.");
                logger.info("Метод settle успешно завершил работу");
            } else if (status == -1) {
                console.showMessage("Комната на обслуживании.");
                logger.error("Ошибка при выполнении метода settle: Комната на обслуживании.");
            } else if (status == -2) {
                console.showMessage("Комната занята.");
                logger.error("Ошибка при выполнении метода settle: Комната занята.");
            } else {
                console.showMessage("Ошибка транзакции.");
                logger.error("Ошибка при выполнении метода settle: Ошибка транзакции.");
            }

        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода settle: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода settle: " + e.getMessage());
        }
    }

    public void evict() {
        try {
            logger.info("Начало выполнения метода evict");
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.isFree(id)) {
                console.showMessage("Комната никем не занята.");
                logger.error("Ошибка при выполнении метода evict: Комната никем не занята.");
                return;
            }
            if (administrator.evict(id)) {
                console.showMessage("Выселение успешно.");
                logger.info("Метод evict успешно завершил работу");
            } else {
                console.showMessage("Ошибка при выселении.");
                logger.error("Ошибка при выполнении метода evict: Ошибка при выселении.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода evict: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода evict: " + e.getMessage());
        }
    }

    public void setAvailable() {
        try {
            logger.info("Начало выполнения метода setAvailable");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                if (roomManagement.getCurrentGuests(roomManagement.getRoom(id)) != null && !roomManagement.getCurrentGuests(roomManagement.getRoom(id)).isEmpty()) {
                    console.showMessage("Сначала нужно выселить гостя.");
                    logger.error("Ошибка при выполнении метода setAvailable: Сначала нужно выселить гостя.");
                } else if (roomManagement.isFree(id)) {
                    console.showMessage("Номер уже свободен.");
                    logger.error("Ошибка при выполнении метода setAvailable: Номер уже свободен.");
                } else {
                    if (roomManagement.setAvailable(id)) {
                        roomDao.save(roomManagement.getRoom(id));
                        console.showMessage("Установка статуса успешна.");
                        logger.info("Метод setAvailable успешно завершил работу");
                    } else {
                        console.showMessage("Включен запрет на смену статуса у номера.");
                        logger.error("Ошибка при выполнении метода setAvailable: Включен запрет на смену статуса у номера.");
                    }
                }
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                logger.error("Ошибка при выполнении метода setAvailable: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода setAvailable: " + e.getMessage());
        }
    }

    public void setOccupied() {
        try {
            logger.info("Начало выполнения метода setOccupied");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                if (roomManagement.isServicing(id)) {
                    console.showMessage("Сначала нужно закончить обслуживание номера.");
                    logger.error("Ошибка при выполнении метода setOccupied: Сначала нужно закончить обслуживание номера.");
                } else if (roomManagement.isOccupied(id)) {
                    console.showMessage("Комната уже занята.");
                    logger.error("Ошибка при выполнении метода setOccupied: Комната уже занята.");
                } else {
                    int daysCount = console.readInt("Введите, на сколько дней установить статус \"занят\": ");
                    if (roomManagement.setStatus(id, daysCount, Status.OCCUPIED)) {
                        roomDao.save(roomManagement.getRoom(id));
                        console.showMessage("Установка статуса успешна.");
                        logger.info("Метод setOccupied успешно завершил работу");
                    } else {
                        console.showMessage("Включен запрет на смену статуса у номера.");
                        logger.error("Ошибка при выполнении метода setOccupied: Включен запрет на смену статуса у номера.");
                    }
                }
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                logger.error("Ошибка при выполнении метода setOccupied: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода setOccupied: " + e.getMessage());
        }
    }

    public void setInService() {
        try {
            logger.info("Начало выполнения метода setInService");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                if (roomManagement.getCurrentGuests(roomManagement.getRoom(id)) != null && !roomManagement.getCurrentGuests(roomManagement.getRoom(id)).isEmpty()) {
                    console.showMessage("Сначала нужно выселить гостя.");
                    logger.error("Ошибка при выполнении метода setInService: Сначала нужно выселить гостя.");
                } else if (roomManagement.isServicing(id)) {
                    console.showMessage("Комната уже на обслуживании.");
                    logger.error("Ошибка при выполнении метода setInService: Комната уже на обслуживании.");
                } else {
                    int daysCount = console.readInt("Введите, на сколько дней установить статус \"на обслуживании\": ");
                    if (roomManagement.setStatus(id, daysCount, Status.IN_SERVICE)) {
                        roomDao.save(roomManagement.getRoom(id));
                        console.showMessage("Установка статуса успешна.");
                        logger.info("Метод setInService успешно завершил работу");
                    } else {
                        console.showMessage("Включен запрет на смену статуса у номера.");
                        logger.error("Ошибка при выполнении метода setInService: Включен запрет на смену статуса у номера.");
                    }
                }
                logger.info("Метод setInService успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                logger.error("Ошибка при выполнении метода setInService: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода setInService: " + e.getMessage());
        }
    }

    public void changeRoomPrice() {
        try {
            logger.info("Начало выполнения метода changeRoomPrice");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                BigDecimal price = console.readBigDecimal("Введите новую стоимость номера: ");
                roomManagement.setNewRoomPrice(id, price);
                roomDao.save(roomManagement.getRoom(id));
                console.showMessage("Изменение успешно.");
                logger.info("Метод changeRoomPrice успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                logger.error("Ошибка при выполнении метода changeRoomPrice: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода changeRoomPrice: " + e.getMessage());
        }
    }

    public void addRoom() {
        try {
            logger.info("Начало выполнения метода addRoom");
            String id = console.readString("Введите id комнаты: ");
            try {
                if (roomManagement.isThereRoom(id)) {
                    console.showMessage("Комната с таким id уже есть.");
                    logger.error("Ошибка при выполнении метода addRoom: Комната с таким id уже есть.");
                    return;
                }
                int number = console.readInt("Введите номер комнаты: ");
                roomManagement.addNewRoom(new Room(id, number, console.readBigDecimal("Введите суточную стоимость номера: "), Status.AVAILABLE, console.readInt("Введите вместимость номера: "), console.readInt("Введите количество звезд: ")));
                roomDao.save(roomManagement.getRoom(id));
                console.showMessage("Добавление успешно.");
                logger.info("Метод addRoom успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                logger.error("Ошибка при выполнении метода getRoomDetailsaddRoom: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода addRoom: " + e.getMessage());
        }
    }

    public void showAllRooms() {
        try {
            logger.info("Начало выполнения метода showAllRooms");
            console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == 1) {
                console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.PRICE));
            } else if (sortType == 2) {
                console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.CAPACITY));
            } else if (sortType == 3) {
                console.showRooms((List<Room>) roomManagement.getAllRoomsWithSort(SortType.STARS));
            } else {
                throw new WrongCommandNumberException();
            }
            logger.info("Метод showAllRooms успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода showAllRooms: " + e.getMessage());
        } catch
        (Exception e) {
            logger.error("Ошибка при выполнении метода showAllRooms: " + e.getMessage());
        }
    }

    public void showAllFreeRooms() {
        try {
            logger.info("Начало выполнения метода showAllFreeRooms");
            console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == 1) {
                console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.PRICE));
            } else if (sortType == 2) {
                console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.CAPACITY));
            } else if (sortType == 3) {
                console.showRooms((List<Room>) roomManagement.getFreeRoomsWithSort(SortType.STARS));
            } else {
                throw new WrongCommandNumberException();
            }
            logger.info("Метод showAllFreeRooms успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода showAllFreeRooms: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода showAllFreeRooms: " + e.getMessage());
        }
    }

    public void getFreeRoomsCount() {
        try {
            logger.info("Начало выполнения метода getFreeRoomsCount");
            console.showMessage(String.valueOf(roomManagement.getFreeRoomsCount()));
            logger.info("Метод getFreeRoomsCount успешно завершил работу");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода getFreeRoomsCount: " + e.getMessage());
        }
    }

    public void showFreeRoomsByDate() {
        try {
            logger.info("Начало выполнения метода showFreeRoomsByDate");
            int daysCount = console.readInt("Введите количество дней от текущей даты: ");
            console.showRooms(roomManagement.getFreeRoomsByDate(new Date(System.currentTimeMillis() + daysCount * RoomManagement.getMSecInDay())));
            logger.info("Метод showFreeRoomsByDate успешно завершил работу");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода showFreeRoomsByDate: " + e.getMessage());
        }
    }

    public void getRoomDetails() {
        try {
            logger.info("Начало выполнения метода getRoomDetails");
            if (roomManagement.getRooms() == null) {
                console.showMessage("Список комнат пуст.");
                logger.error("Ошибка при выполнении метода getRoomDetails: Список комнат пуст.");
                return;
            }
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            console.showMessage(roomManagement.getRoomDetails(id));
            logger.info("Метод getRoomDetails успешно завершил работу");
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода getRoomDetails: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода getRoomDetails: " + e.getMessage());
        }
    }

    public void importRoomData() {
        try {
            logger.info("Начало выполнения метода importRoomData");
            String filePath = console.readString("Введите абсолютный путь к файлу: ");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
                String str;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(";");
                    if (parts.length == 6) {
                        roomManagement.addNewRoom(new Room(parts[0], Integer.parseInt(parts[1]), new BigDecimal(parts[2]), Status.valueOf(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));
                        roomDao.save(roomManagement.getRoom(parts[0]));
                        console.showMessage("Импорт завершен.");
                    } else {
                        console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                        logger.error("Ошибка при выполнении метода importRoomData: Ошибка при импорте, неверное количество параметров в записи.");
                    }
                }
                logger.info("Метод importRoomData успешно завершил работу");
            } catch (NoSuchFileException e) {
                console.showMessage("Файл не найден.");
                logger.error("Ошибка при выполнении метода importRoomData: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Ошибка при выполнении метода importRoomData: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода importRoomData: " + e.getMessage());
        }
    }

    public void exportRoomData() {
        try {
            logger.info("Начало выполнения метода exportRoomData");
            String id = console.readString("Введите id комнаты для экспорта: ");
            if (!roomManagement.isThereRoom(id)) {
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
                        logger.error("Ошибка при выполнении метода exportRoomData: " + e.getMessage());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new RoomNotFoundException();
            }
            logger.info("Метод exportRoomData успешно завершил работу");
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода getRoomDetails: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода exportRoomData: " + e.getMessage());
        }
    }

    public void getThreePrevGuests() {
        try {
            logger.info("Начало выполнения метода getThreePrevGuests");
            RoomManagement roomManagement = administrator.getRoomManagement();
            String id = console.readString("Введите id комнаты: ");
            try {
                if (!roomManagement.isThereRoom(id)) {
                    throw new RoomNotFoundException();
                }
                console.showGuests((List<Guest>) roomManagement.getThreePrevRoomGuests(id));
                logger.info("Метод getThreePrevGuests успешно завершил работу");
            } catch (RoomNotFoundException e) {
                console.showMessage(e.getMessage());
                logger.error("Ошибка при выполнении метода getThreePrevGuests: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода getThreePrevGuests: " + e.getMessage());
        }
    }
}
