package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.model.*;
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
public class GuestController {
    private static final Logger logger = LogManager.getLogger(GuestController.class);

    @Inject
    private Administrator administrator;

    @Inject
    private Console console;

    private boolean running = true;
    private GuestManagement guestManagement;

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
                case 0:
                    running = false;
                    break;

                case 1:
                    showGuests();
                    break;

                case 2:
                    getGuestsCount();
                    break;

                case 3:
                    getTotalCost();
                    break;

                case 4:
                    useService();
                    break;

                case 5:
                    showServicesUsedByGuest();
                    break;

                case 6:
                    importGuestData();
                    break;

                case 7:
                    exportGuestData();
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }


    public void showGuests() {
        try {
            logger.info("Начало выполнения метода showGuests");
            console.showMessage("1. Алфавит;\n2. Дата освобождения номера.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == 1) {
                console.showGuests((List<Guest>) guestManagement.getActualGuestsWithSort(SortType.ALPHABET));
            } else if (sortType == 2) {
                console.showGuests((List<Guest>) guestManagement.getActualGuestsWithSort(SortType.DATE));
            } else {
                throw new WrongCommandNumberException();
            }
            logger.info("Метод showGuests успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода showGuests: " + e.getMessage());
        }
        catch (Exception e) {
            logger.error("Ошибка при выполнении метода showGuests: " + e.getMessage());
        }
    }

    public void getGuestsCount() {
        try {
            logger.info("Начало выполнения метода getGuestsCount");
            console.showMessage(String.valueOf(guestManagement.getGuestsCount()));
            logger.info("Метод getGuestsCount успешно завершил работу");
        }
        catch (Exception e) {
            logger.error("Ошибка при выполнении метода getGuestsCount: " + e.getMessage());
        }
    }

    public void importGuestData() {
        try {
            logger.info("Начало выполнения метода importGuestData");
            String filePath = console.readString("Введите абсолютный путь к файлу: ");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
                String str;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(";");
                    if (parts.length == 3) {
                        guestManagement.addGuest(new Guest(parts[0], parts[1], Integer.parseInt(parts[2])));
                        logger.info("Метод importGuestData успешно завершил работу");
                    } else {
                        console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                        logger.error("Ошибка при выполнении метода importGuestData: Неверное количество параметров в записи.");
                    }
                }
                console.showMessage("Импорт завершен.");
            } catch (NoSuchFileException e) {
                console.showMessage("Файл не найден.");
                logger.error("Ошибка при выполнении метода importGuestData: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Ошибка при выполнении метода importGuestData: " + e.getMessage());
            }
        }
        catch (Exception e) {
            logger.error("Ошибка при выполнении метода importGuestData: " + e.getMessage());
        }
    }

    public void exportGuestData() {
        try {
            logger.info("Начало выполнения метода exportGuestData");
            String id = console.readString("Введите id гостя для экспорта: ");
            if (guestManagement.isThereGuest(id)) {
                String dirPath = console.readString("Введите абсолютный путь к папке для экспорта: ");
                File directory = new File(dirPath);
                directory.mkdirs();
                File file = new File(directory, "guest_export.csv");
                try {
                    file.createNewFile();
                    Guest guest = guestManagement.getGuest(id);
                    String[] data = new String[3];
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
                        logger.error("Ошибка при выполнении метода exportGuestData: " + e.getMessage());
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                } else {
                    throw new GuestNotFoundException();
                }
                logger.info("Метод exportGuestData успешно завершил работу");
            }
        catch (GuestNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода exportGuestData: " + e.getMessage());
        }
        catch (Exception e) {
            logger.error("Ошибка при выполнении метода exportGuestData: " + e.getMessage());
        }
    }

    public void showServicesUsedByGuest() {
        try {
            logger.info("Начало выполнения метода showServicesUsedByGuest");
            GuestManagement guestManagement = administrator.getGuestManagement();
            UsedServiceManagement usedServiceManagement = administrator.getUsedServiceManagement();
            String guestId = console.readString("Введите id гостя: ");
            if (guestManagement.isThereGuest(guestId)) {
                List<UsedService> usedServices = usedServiceManagement.getUsedServices(guestManagement.getGuest(guestId));
                console.showMessage("1. Цена;\n2. Дата.");
                int sortType = console.readInt("Выберите вид сортировки: ");
                if (sortType == 1) {
                    console.showUsedServices(usedServiceManagement.getUsedServicesByGuestWithSort(usedServices, SortType.PRICE));
                } else if (sortType == 2) {
                    console.showUsedServices(usedServiceManagement.getUsedServicesByGuestWithSort(usedServices, SortType.DATE));
                } else {
                    throw new WrongCommandNumberException();
                }
            } else {
                throw new GuestNotFoundException();
            }
            logger.info("Метод showServicesUsedByGuest успешно завершил работу");
        }
        catch (WrongCommandNumberException | GuestNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода showServicesUsedByGuest: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода showServicesUsedByGuest: " + e.getMessage());
        }
    }

    public void getTotalCost() {
        try {
            logger.info("Начало выполнения метода getTotalCost");
            RoomManagement roomManagement = administrator.getRoomManagement();
            String id = console.readString("Введите id комнаты: ");
            if (!roomManagement.isThereRoom(id)) {
                throw new RoomNotFoundException();
            }
            if (roomManagement.isOccupied(id) && roomManagement.getCurrentGuests(roomManagement.getRoom(id)) != null && !roomManagement.getCurrentGuests(roomManagement.getRoom(id)).isEmpty()) {
                console.showMessage(String.valueOf(roomManagement.getTotalRoomCost(id)));
                logger.info("Метод getTotalCost успешно завершил работу");
            } else {
                console.showMessage("В номере никто не живет.");
                logger.error("Ошибка при выполнении метода getTotalCost: В номере никто не живет.");
            }
        } catch (RoomNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода getTotalCost: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Ошибка при выполнении метода getTotalCost: " + ex.getMessage());
        }
    }

    public void useService() {
        try {
            logger.info("Начало выполнения метода useService");
            GuestManagement guestManagement = administrator.getGuestManagement();
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
            logger.info("Метод useService успешно завершил работу");
        } catch (ServiceNotFoundException | GuestNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода useService: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода useService: " + e.getMessage());
        }
    }
}
