package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.constants.CommandConstants;
import org.hotel.constants.ParametersConstants;
import org.hotel.model.Administrator;
import org.hotel.model.Priceable;
import org.hotel.model.Service;
import org.hotel.model.ServiceManagement;
import org.hotel.model.ServiceNotFoundException;
import org.hotel.model.ServiceSection;
import org.hotel.model.SortType;
import org.hotel.model.WrongCommandNumberException;
import org.hotel.model.WrongServiceTypeNumberException;
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
public final class ServiceController {
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
     * Управление услугами.
     */
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
                case CommandConstants.COMMAND_ZERO:
                    running = false;
                    break;

                case CommandConstants.COMMAND_ONE:
                    addService();
                    break;

                case CommandConstants.COMMAND_TWO:
                    changeServicePrice();
                    break;

                case CommandConstants.COMMAND_THREE:
                    showServices();
                    break;

                case CommandConstants.COMMAND_FOUR:
                    showCatalog();
                    break;

                case CommandConstants.COMMAND_FIVE:
                    importServiceData();
                    break;

                case CommandConstants.COMMAND_SIX:
                    exportServiceData();
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }

    public void changeServicePrice() {
        try {
            LOGGER.info("Начало выполнения метода changeServicePrice");
            if (serviceManagement.getServices() == null || serviceManagement.getServices().isEmpty()) {
                console.showMessage("Список услуг пуст.");
                LOGGER.error("Ошибка при выполнении метода changeServicePrice: Список услуг пуст.");
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
                console.showMessage("Изменение успешно.");
            }
            LOGGER.info("Метод changeServicePrice успешно завершил работу");
        } catch (ServiceNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода changeServicePrice: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода changeServicePrice: " + e.getMessage());
        }
    }

    public void addService() {
        try {
            LOGGER.info("Начало выполнения метода addService");
            String id = console.readString("Введите id услуги: ");
            List<Service> services = new ArrayList<>(serviceManagement.getServices());
            Optional<Service> service = services.stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst();
            if (service.isPresent()) {
                console.showMessage("Услуга с таким id уже есть.");
                LOGGER.error("Ошибка при выполнении метода addService: Услуга с таким id уже есть.");
            } else {
                String newServiceName = console.readString("Введите название услуги: ");
                console.showMessage("1. Питание;\n2. Транспортные услуги;\n3. Уборка;\n4. Здоровье;\n5. Бизнес;\n6. Дети.");
                int sectionType = console.readInt("Введите номер типа услуги: ");
                Service newService;
                switch (sectionType) {
                    case CommandConstants.COMMAND_ONE:
                        newService = new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.FOOD);
                        serviceManagement.addNewService(newService);
                        break;
                    case CommandConstants.COMMAND_TWO:
                        newService = new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.PARKING);
                        serviceManagement.addNewService(newService);
                        break;
                    case CommandConstants.COMMAND_THREE:
                        newService = new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.CLEANING);
                        serviceManagement.addNewService(newService);
                        break;
                    case CommandConstants.COMMAND_FOUR:
                        newService = new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.HEALTH);
                        serviceManagement.addNewService(newService);
                        break;
                    case CommandConstants.COMMAND_FIVE:
                        newService = new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.BUSINESS);
                        serviceManagement.addNewService(newService);
                        break;
                    case CommandConstants.COMMAND_SIX:
                        newService = new Service(id, newServiceName, console.readBigDecimal("Введите стоимость услуги: "), ServiceSection.KIDS);
                        serviceManagement.addNewService(newService);
                        break;
                    default:
                        throw new WrongServiceTypeNumberException();
                }
                console.showMessage("Добавление услуги успешно.");
                LOGGER.info("Метод addService успешно завершил работу");
            }
        } catch (WrongServiceTypeNumberException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода addService: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода addService: " + e.getMessage());
        }
    }

    public void showServices() {
        try {
            LOGGER.info("Начало выполнения метода showServices");
            console.showMessage("1. Цена;\n2. Раздел.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == CommandConstants.COMMAND_ONE) {
                console.showServices((List<Service>) serviceManagement.getServicesWithSort(SortType.PRICE));
            } else if (sortType == CommandConstants.COMMAND_TWO) {
                console.showServices((List<Service>) serviceManagement.getServicesWithSort(SortType.SECTION));
            } else {
                throw new WrongCommandNumberException();
            }
            LOGGER.info("Метод showServices успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода showServices: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showServices: " + e.getMessage());
        }
    }

    public void showCatalog() {
        try {
            LOGGER.info("Начало выполнения метода showCatalog");
            console.showMessage("1. Цена;\n2. Раздел.");
            int sortType = console.readInt("Выберите вид сортировки: ");
            if (sortType == CommandConstants.COMMAND_ONE) {
                console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.PRICE));
            } else if (sortType == CommandConstants.COMMAND_TWO) {
                console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.SECTION));
            } else {
                throw new WrongCommandNumberException();
            }
            LOGGER.info("Метод showCatalog успешно завершил работу");
        } catch (WrongCommandNumberException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода showCatalog: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода showCatalog: " + e.getMessage());
        }
    }

    public void importServiceData() {
        try {
            LOGGER.info("Начало выполнения метода importServiceData");
            String filePath = console.readString("Введите абсолютный путь к файлу: ");
            try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath), Charset.forName("windows-1251"))) {
                String str;
                while ((str = br.readLine()) != null) {
                    String[] parts = str.split(";");
                    if (parts.length == ParametersConstants.SERVICE_PARAMETERS_COUNT) {
                        serviceManagement.addNewService(new Service(parts[CommandConstants.COMMAND_ZERO], parts[CommandConstants.COMMAND_ONE], new BigDecimal(parts[CommandConstants.COMMAND_TWO]), ServiceSection.valueOf(parts[CommandConstants.COMMAND_THREE])));
                        console.showMessage("Импорт завершен.");
                        LOGGER.info("Метод importServiceData успешно завершил работу");
                    } else {
                        console.showMessage("Ошибка при импорте, неверное количество параметров в записи.");
                        LOGGER.error("Ошибка при выполнении метода importServiceData: Ошибка при импорте, неверное количество параметров в записи.");
                    }
                }
            } catch (NoSuchFileException e) {
                console.showMessage("Файл не найден.");
                LOGGER.error("Ошибка при выполнении метода importServiceData: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода importServiceData: " + e.getMessage());
        }
    }

    public void exportServiceData() {
        try {
            LOGGER.info("Начало выполнения метода importServiceData");
            String id = console.readString("Введите id услуги для экспорта: ");
            if (serviceManagement.isThereService(id)) {
                String dirPath = console.readString("Введите абсолютный путь к папке для экспорта: ");
                File directory = new File(dirPath);
                directory.mkdirs();
                File file = new File(directory, "service_export.csv");
                try {
                    file.createNewFile();
                    Service service = serviceManagement.getService(id);
                    String[] data = new String[ParametersConstants.SERVICE_PARAMETERS_COUNT];
                    data[CommandConstants.COMMAND_ZERO] = id;
                    data[CommandConstants.COMMAND_ONE] = String.valueOf(service.getName());
                    data[CommandConstants.COMMAND_TWO] = String.valueOf(service.getPrice());
                    data[CommandConstants.COMMAND_THREE] = service.getServiceSection().name();
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
            LOGGER.info("Метод importServiceData успешно завершил работу");
        } catch (ServiceNotFoundException e) {
            console.showMessage(e.getMessage());
            LOGGER.error("Ошибка при выполнении метода changeServicePrice: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении метода importServiceData: " + e.getMessage());
        }
    }
}
