package controller;

import model.*;
import view.Console;

import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceController {
    private Console console;
    private boolean running = true;
    private Administrator administrator;
    private ServiceManagement serviceManagement;

    public ServiceController(Administrator administrator, Console console) {
        this.console = console;
        this.administrator = administrator;
        this.serviceManagement = administrator.getServiceManagement();
    }

    public void run() {
        running = true;
        while (running) {
            console.printServiceMenu();
            int command = console.readInt("Введите номер команды: ");
            switch (command) {
                case 0:
                    running = false;
                    break;

                case 1:
                    addService();
                    break;

                case 2:
                    changeServicePrice();
                    break;

                case 3:
                    showServices();
                    break;

                case 4:
                    showCatalog();
                    break;

                case 5:
                    importServiceData();
                    break;

                case 6:
                    exportServiceData();
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }


    public void changeServicePrice() {
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

    public void addService() {
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

    public void showServices() {
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

    public void showCatalog() {
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

    public void importServiceData() {
        String filePath = console.readString("Введите абсолютный путь к файлу: ");
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
            String str;
            while ((str = br.readLine()) != null) {
                String[] parts = str.split(";");
                if (parts.length == 4) {
                    serviceManagement.addNewService(new Service(parts[0], parts[1], Double.parseDouble(parts[2]), ServiceSection.valueOf(parts[3])));
                } else {
                    console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                }
            }
            console.showMessage("Импорт завершен.");
        } catch (NoSuchFileException e) {
            console.showMessage("Файл не найден.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportServiceData(){
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
