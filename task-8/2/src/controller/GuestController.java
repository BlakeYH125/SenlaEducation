package controller;

import annotations.Component;
import annotations.Inject;
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
import java.util.List;

@Component
public class GuestController {
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

    public void getGuestsCount() {
        console.showMessage(String.valueOf(guestManagement.getGuestsCount()));
    }

    public void importGuestData() {
        String filePath = console.readString("Введите абсолютный путь к файлу: ");
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
            String str;
            while ((str = br.readLine()) != null) {
                String[] parts = str.split(";");
                if (parts.length == 3) {
                    guestManagement.addGuest(new Guest(parts[0], parts[1], Integer.parseInt(parts[2])));
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

    public void exportGuestData() {
        String id = console.readString("Введите id гостя для экспорта: ");
        if (guestManagement.getGuests().containsKey(id)) {
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
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            console.showMessage("Гостя с таким id нет.");
        }
    }

    public void showServicesUsedByGuest() {
        GuestManagement guestManagement = administrator.getGuestManagement();
        String guestId = console.readString("Введите id гостя: ");
        if (guestManagement.getGuests().containsKey(guestId)) {
            List<UsedService> usedServices = guestManagement.getGuest(guestId).getUsedServices();
            console.showMessage("1. Цена;\n2. Дата.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == 1) {
                console.showUsedServices(guestManagement.getUsedServicesByGuestWithSort(usedServices, SortType.PRICE));
            } else if (sortType == 2) {
                console.showUsedServices(guestManagement.getUsedServicesByGuestWithSort(usedServices, SortType.DATE));
            } else {
                console.showMessage("Неверный номер команды.");
            }
        } else {
            console.showMessage("Гостя с таким id нет.");
        }
    }

    public void getTotalCost() {
        RoomManagement roomManagement = administrator.getRoomManagement();
        String id = console.readString("Введите id комнаты: ");
        if (!roomManagement.getRooms().containsKey(id)) {
            throw new RoomNotFoundException();
        }
        if (roomManagement.isOccupied(id) && roomManagement.getRoom(id).getGuests() != null && !roomManagement.getRoom(id).getGuests().isEmpty()) {
            console.showMessage(String.valueOf(roomManagement.getTotalRoomCost(id)));
        } else {
            console.showMessage("В номере никто не живет.");
        }
    }

    public void useService() {
        GuestManagement guestManagement = administrator.getGuestManagement();
        ServiceManagement serviceManagement = administrator.getServiceManagement();
        String guestId = console.readString("Введите id гостя: ");
        if (guestManagement.getGuests().containsKey(guestId)) {
            String serviceId = console.readString("Введите id услуги: ");
            if (serviceManagement.getServices().containsKey(serviceId)) {
                administrator.useServiceByGuest(guestId, serviceId);
                console.showMessage("Использование услуги успешно.");
            } else {
                console.showMessage("Услуги в таким id нет.");
            }
        } else {
            console.showMessage("Гостя с таким id нет.");
        }
    }
}
