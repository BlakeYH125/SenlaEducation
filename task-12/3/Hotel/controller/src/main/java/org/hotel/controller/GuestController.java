package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.model.Administrator;
import org.hotel.model.Guest;
import org.hotel.model.GuestManagement;
import org.hotel.model.GuestNotFoundException;
import org.hotel.model.RoomManagement;
import org.hotel.model.RoomNotFoundException;
import org.hotel.model.ServiceManagement;
import org.hotel.model.ServiceNotFoundException;
import org.hotel.model.SortType;
import org.hotel.model.UsedService;
import org.hotel.model.UsedServiceManagement;
import org.hotel.model.WrongCommandNumberException;
import org.hotel.view.Console;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

@Component
public final class GuestController {
    /**
     * Логгер для фиксации логов.
     */
    private static final Logger LOGGER = LogManager.getLogger(GuestController.class);

    /**
     * Администратор.
     */
    @Inject
    private Administrator administrator;

    /**
     * Вывод на консоль.
     */
    @Inject
    private Console console;

    /**
     * Индикатор работы.
     */
    private boolean running = true;

    /**
     * Управление гостями.
     */
    private GuestManagement guestManagement;

    /**
     * Команда 0.
     */
    private static final int COMMAND_ZERO = 0;

    /**
     * Команда 1.
     */
    private static final int COMMAND_ONE = 1;

    /**
     * Команда 2.
     */
    private static final int COMMAND_TWO = 2;

    /**
     * Команда 3.
     */
    private static final int COMMAND_THREE = 3;

    /**
     * Команда 4.
     */
    private static final int COMMAND_FOUR = 4;

    /**
     * Команда 5.
     */
    private static final int COMMAND_FIVE = 5;

    /**
     * Команда 6.
     */
    private static final int COMMAND_SIX = 6;

    /**
     * Команда 7.
     */
    private static final int COMMAND_SEVEN = 7;

    /**
     * Количество параметров.
     */
    private static final int PARAMETERS_COUNT = 3;

    public GuestController() {
    }

    public void init() {
        this.guestManagement = administrator.getGuestManagement();
    }

    public void run() {
        running = true;
        while (running) {
            console.printGuestMenu();
            int command = console.readInt("Введите номер команды: ");
            switch (command) {
                case COMMAND_ZERO:
                    running = false;
                    break;

                case COMMAND_ONE:
                    showGuests();
                    break;

                case COMMAND_TWO:
                    getGuestsCount();
                    break;

                case COMMAND_THREE:
                    getTotalCost();
                    break;

                case COMMAND_FOUR:
                    useService();
                    break;

                case COMMAND_FIVE:
                    showServicesUsedByGuest();
                    break;

                case COMMAND_SIX:
                    importGuestData();
                    break;

                case COMMAND_SEVEN:
                    exportGuestData();
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }


    public void showGuests() {
        try {
            LOGGER.info("Начало выполнения метода showGuests");
            console.showMessage("1. Алфавит;\n2. Дата освобождения номера.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == COMMAND_ONE) {
                console.showGuests((List<Guest>) guestManagement.getActualGuestsWithSort(SortType.ALPHABET));
            } else if (sortType == COMMAND_TWO) {
                console.showGuests((List<Guest>) guestManagement.getActualGuestsWithSort(SortType.DATE));
            } else {
                throw new WrongCommandNumberException();
            }
            LOGGER.info("Метод showGuests успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода showGuests: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showGuests: " + e.getMessage());
        }
    }

    public void getGuestsCount() {
        try {
            LOGGER.info("Начало выполнения метода getGuestsCount");
            console.showMessage(String.valueOf(guestManagement.getGuestsCount()));
            LOGGER.info("Метод getGuestsCount успешно завершил работу");
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода getGuestsCount: " + e.getMessage());
        }
    }

    public void importGuestData() {
        try {
            LOGGER.info("Начало выполнения метода importGuestData");
            String filePath = console.readString("Введите абсолютный путь к файлу: ");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
                String str;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(";");
                    if (parts.length == PARAMETERS_COUNT) {
                        guestManagement.addGuest(new Guest(parts[0], parts[1], Integer.parseInt(parts[2])));
                        LOGGER.info("Метод importGuestData успешно завершил работу");
                    } else {
                        console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                        LOGGER.error("Ошибка при выполнении метода importGuestData: Неверное количество параметров в записи.");
                    }
                }
                console.showMessage("Импорт завершен.");
            } catch (NoSuchFileException e) {
                console.showMessage("Файл не найден.");
                LOGGER.error("Ошибка при выполнении метода importGuestData: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Ошибка при выполнении метода importGuestData: " + e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода importGuestData: " + e.getMessage());
        }
    }

    public void exportGuestData() {
        try {
            LOGGER.info("Начало выполнения метода exportGuestData");
            String id = console.readString("Введите id гостя для экспорта: ");
            if (guestManagement.isThereGuest(id)) {
                String dirPath = console.readString("Введите абсолютный путь к папке для экспорта: ");
                File directory = new File(dirPath);
                directory.mkdirs();
                File file = new File(directory, "guest_export.csv");
                try {
                    file.createNewFile();
                    Guest guest = guestManagement.getGuest(id);
                    String[] data = new String[PARAMETERS_COUNT];
                    data[0] = id;
                    data[1] = guest.getFullName();
                    data[2] = String.valueOf(guest.getAge());
                    String result = String.join(";", data);
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                        bw.write(result);
                        bw.newLine();
                        console.showMessage("Экспорт успешен.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        LOGGER.error("Ошибка при выполнении метода exportGuestData: " + e.getMessage());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new GuestNotFoundException();
            }
            LOGGER.info("Метод exportGuestData успешно завершил работу");
        } catch (GuestNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода exportGuestData: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода exportGuestData: " + e.getMessage());
        }
    }

    public void showServicesUsedByGuest() {
        try {
            LOGGER.info("Начало выполнения метода showServicesUsedByGuest");
            UsedServiceManagement usedServiceManagementP = administrator.getUsedServiceManagement();
            String guestId = console.readString("Введите id гостя: ");
            if (guestManagement.isThereGuest(guestId)) {
                List<UsedService> usedServices = usedServiceManagementP.getUsedServices(guestManagement.getGuest(guestId));
                console.showMessage("1. Цена;\n2. Дата.");
                int sortType = console.readInt("Выберите вид сортировки: ");
                if (sortType == COMMAND_ONE) {
                    console.showUsedServices(usedServiceManagementP.getUsedServicesByGuestWithSort(usedServices, SortType.PRICE));
                } else if (sortType == COMMAND_TWO) {
                    console.showUsedServices(usedServiceManagementP.getUsedServicesByGuestWithSort(usedServices, SortType.DATE));
                } else {
                    throw new WrongCommandNumberException();
                }
            } else {
                throw new GuestNotFoundException();
            }
            LOGGER.info("Метод showServicesUsedByGuest успешно завершил работу");
        } catch (WrongCommandNumberException | GuestNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода showServicesUsedByGuest: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showServicesUsedByGuest: " + e.getMessage());
        }
    }

    public void getTotalCost() {
        try {
            LOGGER.info("Начало выполнения метода getTotalCost");
            RoomManagement roomManagement = administrator.getRoomManagement();
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.isOccupied(id) && roomManagement.getCurrentGuests(roomManagement.getRoom(id)) != null && !roomManagement.getCurrentGuests(roomManagement.getRoom(id)).isEmpty()) {
                console.showMessage(String.valueOf(roomManagement.getTotalRoomCost(id)));
                LOGGER.info("Метод getTotalCost успешно завершил работу");
            } else {
                console.showMessage("В номере никто не живет.");
                LOGGER.error("Ошибка при выполнении метода getTotalCost: В номере никто не живет.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода getTotalCost: " + e.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Ошибка при выполнении метода getTotalCost: " + ex.getMessage());
        }
    }

    public void useService() {
        try {
            LOGGER.info("Начало выполнения метода useService");
            ServiceManagement serviceManagement = administrator.getServiceManagement();
            String guestId = console.readString("Введите id гостя: ");
            if (guestManagement.isThereGuest(guestId)) {
                String serviceId = console.readString("Введите id услуги: ");
                if (serviceManagement.isThereService(serviceId)) {
                    administrator.useServiceByGuest(guestId, serviceId);
                    console.showMessage("Использование услуги успешно.");
                } else {
                    throw new ServiceNotFoundException();
                }
            } else {
                throw new GuestNotFoundException();
            }
            LOGGER.info("Метод useService успешно завершил работу");
        } catch (ServiceNotFoundException | GuestNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода useService: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода useService: " + e.getMessage());
        }
    }
}
