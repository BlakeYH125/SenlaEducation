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

    public void importServiceData(Console console, GuestManagement guestManagement) {
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

    public void exportServiceData(Console console, GuestManagement guestManagement) {
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

    public void useService(Console console, Administrator administrator) {
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

    public void showServicesUsedByGuest(Console console, Administrator administrator) {
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
}
