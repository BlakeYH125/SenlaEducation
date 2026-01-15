package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.dao.ServiceDao;
import org.hotel.model.*;
import org.hotel.view.Console;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
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

@Component
public class ServiceController {
    private static final Logger logger = LogManager.getLogger(ServiceController.class);

    @Inject
    private Administrator administrator;

    @Inject
    private Console console;

    @Inject
    private ServiceDao serviceDao;

    private boolean running = true;
    private ServiceManagement serviceManagement;

    public ServiceController() {
    }

    public void init() {
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
        try {
            logger.info("Начало выполнения метода changeServicePrice");
            if (serviceManagement.getServices() == null || serviceManagement.getServices().isEmpty()) {
                console.showMessage("Список услуг пуст.");
                logger.error("Ошибка при выполнении метода changeServicePrice: Список услуг пуст.");
                return;
            }
            List<Service> services = new ArrayList<>(serviceManagement.getServices());
            console.showServices(services);
            String id = console.readString("Введите id услуги: ");
            Optional<Service> service = services.stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst();
            if (!service.isPresent()) {
                throw new ServiceNotFoundException();
            } else {
                BigDecimal price = console.readBigDecimal("Введите новую стоимость услуги: ");
                serviceManagement.setNewServicePrice(id, price);
                serviceDao.save(serviceManagement.getService(id));
                console.showMessage("Изменение успешно.");
            }
            logger.info("Метод changeServicePrice успешно завершил работу");
        } catch (ServiceNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода changeServicePrice: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода changeServicePrice: " + e.getMessage());
        }
    }

    public void addService() {
        try {
            logger.info("Начало выполнения метода addService");
            String id = console.readString("Введите id услуги: ");
            List<Service> services = new ArrayList<>(serviceManagement.getServices());
            Optional<Service> service = services.stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst();
            if (service.isPresent()) {
                console.showMessage("Услуга с таким id уже есть.");
                logger.error("Ошибка при выполнении метода addService: Услуга с таким id уже есть.");
            } else {
                String newServiceName = console.readString("Введите название услуги: ");
                console.showMessage("1. Питание;\n2. Транспортные услуги;\n3. Уборка;\n4. Здоровье;\n5. Бизнес;\n6. Дети.");
                int sectionType = console.readInt("Введите номер типа услуги: ");
                Service newService;
                switch (sectionType) {
                    case 1:
                        newService =  new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.FOOD);
                        serviceManagement.addNewService(newService);
                        serviceDao.save(newService);
                        break;
                    case 2:
                        newService =  new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.PARKING);
                        serviceManagement.addNewService(newService);
                        serviceDao.save(newService);
                        break;
                    case 3:
                        newService =  new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.CLEANING);
                        serviceManagement.addNewService(newService);
                        serviceDao.save(newService);
                        break;
                    case 4:
                        newService =  new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.HEALTH);
                        serviceManagement.addNewService(newService);
                        serviceDao.save(newService);
                        break;
                    case 5:
                        newService =  new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.BUSINESS);
                        serviceManagement.addNewService(newService);
                        serviceDao.save(newService);
                        break;
                    case 6:
                        newService =  new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.KIDS);
                        serviceManagement.addNewService(newService);
                        serviceDao.save(newService);
                        break;
                    default:
                        throw new WrongServiceTypeNumberException();
                }
                console.showMessage("Добавление услуги успешно.");
                logger.info("Метод addService успешно завершил работу");
            }
        } catch (WrongServiceTypeNumberException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода addService: " + e.getMessage());
        }
        catch (Exception e) {
            logger.error("Ошибка при выполнении метода addService: " + e.getMessage());
        }
    }

    public void showServices() {
        try {
            logger.info("Начало выполнения метода showServices");
            console.showMessage("1. Цена;\n2. Раздел.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == 1) {
                console.showServices((List<Service>) serviceManagement.getServicesWithSort(SortType.PRICE));
            } else if (sortType == 2) {
                console.showServices((List<Service>) serviceManagement.getServicesWithSort(SortType.SECTION));
            } else {
                throw new WrongCommandNumberException();
            }
            logger.info("Метод showServices успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода showServices: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода showServices: " + e.getMessage());
        }
    }

    public void showCatalog() {
        try {
            logger.info("Начало выполнения метода showCatalog");
            console.showMessage("1. Цена;\n2. Раздел.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == 1) {
                console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.PRICE));
            } else if (sortType == 2) {
                console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.SECTION));
            } else {
                throw new WrongCommandNumberException();
            }
            logger.info("Метод showCatalog успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода showCatalog: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода showCatalog: " + e.getMessage());
        }
    }

    public void importServiceData() {
        try {
            logger.info("Начало выполнения метода importServiceData");
            String filePath = console.readString("Введите абсолютный путь к файлу: ");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
                String str;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(";");
                    if (parts.length == 4) {
                        serviceManagement.addNewService(new Service(parts[0], parts[1], new BigDecimal(parts[2]), ServiceSection.valueOf(parts[3])));
                        console.showMessage("Импорт завершен.");
                        logger.info("Метод importServiceData успешно завершил работу");
                    } else {
                        console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                        logger.error("Ошибка при выполнении метода importServiceData: Ошибка при импорте, неверное количество параметров в записи.");
                    }
                }

            } catch (NoSuchFileException e) {
                console.showMessage("Файл не найден.");
                logger.error("Ошибка при выполнении метода importServiceData: " + e.getMessage());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода importServiceData: " + e.getMessage());
        }
    }

    public void exportServiceData(){
        try {
            logger.info("Начало выполнения метода importServiceData");
            String id = console.readString("Введите id услуги для экспорта: ");
            if (serviceManagement.isThereService(id)) {
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
                throw new ServiceNotFoundException();
            }
            logger.info("Метод importServiceData успешно завершил работу");
        } catch (ServiceNotFoundException e) {
            console.showMessage(e.getMessage());
            logger.error("Ошибка при выполнении метода changeServicePrice: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Ошибка при выполнении метода importServiceData: " + e.getMessage());
        }
    }
}
