package controller;

import model.*;
import view.Console;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ServiceController {
    public void changeServicePrice(Console console, ServiceManagement serviceManagement) {
        if (serviceManagement.getServices() == null || serviceManagement.getServices().isEmpty()) {
            console.showMessage("Список услуг пуст.");
            return;
        }
        List<Service> services = new ArrayList<>(serviceManagement.getServices().values());
        console.showServices(services);
        String id = console.readString("Введите id услуги: ");
        Optional<Service> service = services.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        if (!service.isPresent()) {
            console.showMessage("Услуги с таким id нет.");
        } else {
            double price = console.readDouble("Введите новую стоимость услуги: ");
            serviceManagement.setNewServicePrice(id, price);
            console.showMessage("Изменение успешно.");
        }
    }

    public void addService(Console console, ServiceManagement serviceManagement) {
        String id = console.readString("Введите id услуги: ");
        List<Service> services = new ArrayList<>(serviceManagement.getServices().values());
        Optional<Service> service = services.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
        if (service.isPresent()) {
            console.showMessage("Услуга с таким id уже есть.");
        } else {
            String newServiceName = console.readString("Введите название услуги: ");
            console.showMessage("1. Питание;\n2. Транспортные услуги;\n3. Уборка;\n4. Здоровье;\n5. Бизнес;\n6. Дети.");
            int sectionType = console.readInt("Введите номер типа услуги: ");
            switch (sectionType) {
                case 1:
                    serviceManagement.addNewService(new Service(id, newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.FOOD));
                    break;
                case 2:
                    serviceManagement.addNewService(new Service(id, newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.PARKING));
                    break;
                case 3:
                    serviceManagement.addNewService(new Service(id, newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.CLEANING));
                    break;
                case 4:
                    serviceManagement.addNewService(new Service(id, newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.HEALTH));
                    break;
                case 5:
                    serviceManagement.addNewService(new Service(id, newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.BUSINESS));
                    break;
                case 6:
                    serviceManagement.addNewService(new Service(id, newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.KIDS));
                    break;
                default:
                    console.showMessage("Неверный номер типа услуги.");
            }
            console.showMessage("Добавление услуги успешно.");
        }
    }

    public void showServices(Console console, ServiceManagement serviceManagement) {
        console.showMessage("1. Цена;\n2. Раздел.");
        int sortType = console.readInt("Выберите вид сортировки: ");
        if (sortType == 1) {
            console.showServices((List<Service>) serviceManagement.getServicesWithSort(SortType.PRICE));
        } else if (sortType == 2) {
            console.showServices((List<Service>) serviceManagement.getServicesWithSort(SortType.SECTION));
        } else {
            console.showMessage("Неверный номер команды.");
        }
    }

    public void showCatalog(Console console, Administrator administrator) {
        console.showMessage("1. Цена;\n2. Раздел.");
        int sortType = console.readInt("Выберите вид сортировки: ");
        if (sortType == 1) {
            console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.PRICE));
        } else if (sortType == 2) {
            console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.SECTION));
        } else {
            console.showMessage("Неверный номер команды.");
        }
    }

    public void importServiceData(Console console, ServiceManagement serviceManagement) {
        String filePath = console.readString("Введите абсолютный путь к файлу: ");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String str;
            while ((str = br.readLine()) != null) {
                String[] parts = str.split(";");
                serviceManagement.addNewService(new Service(parts[0], parts[1], Double.parseDouble(parts[2]), ServiceSection.valueOf(parts[3])));
            }
            console.showMessage("Импорт успешен.");
        } catch (FileNotFoundException e) {
            console.showMessage("Файл не найден.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportServiceData(Console console, ServiceManagement serviceManagement){
        String id = console.readString("Введите id услуги для экспорта: ");
        if (serviceManagement.getServices().containsKey(id)) {
            String dirPath = console.readString("Введите абсолютный путь к папке для экспорта: ");
            File directory = new File(dirPath);
            directory.mkdirs();
            File file = new File(directory, "service_export.csv");
            try {
                file.createNewFile();
                Service service = serviceManagement.getService(id);
                String[] data = new String[4];
                data[0] = id;
                data[1] = String.valueOf(service.getName());
                data[2] = String.valueOf(service.getPrice());
                data[3] = service.getServiceSection().name();
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
            console.showMessage("Услуги с таким id нет.");
        }
    }
}
