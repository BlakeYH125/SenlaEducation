import education.task5.model.*;
import education.task5.view.Console;

import java.util.ArrayList;
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
        String serviceName = console.readString("Введите название услуги: ");
        Optional<Service> service = services.stream()
                .filter(s -> s.getName().equalsIgnoreCase(serviceName))
                .findFirst();
        if (!service.isPresent()) {
            console.showMessage("Такой услуги нет.");
        } else {
            double price = console.readDouble("Введите новую стоимость услуги: ");
            serviceManagement.setNewServicePrice(serviceName, price);
            console.showMessage("Изменение успешно.");
        }
    }

    public void addService(Console console, ServiceManagement serviceManagement) {
        List<Service> services = new ArrayList<>(serviceManagement.getServices().values());
        String newServiceName = console.readString("Введите название услуги: ");
        Optional<Service> service = services.stream()
                .filter(s -> s.getName().equalsIgnoreCase(newServiceName))
                .findFirst();
        if (service.isPresent()) {
            console.showMessage("Такая услуга уже есть.");
        } else {
            console.showMessage("1. Питание;\n2. Транспортные услуги;\n3. Уборка;\n4. Здоровье;\n5. Бизнес;\n6. Дети.");
            int sectionType = console.readInt("Введите номер типа услуги: ");
            switch (sectionType) {
                case 1:
                    serviceManagement.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.FOOD));
                    break;
                case 2:
                    serviceManagement.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.PARKING));
                    break;
                case 3:
                    serviceManagement.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.CLEANING));
                    break;
                case 4:
                    serviceManagement.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.HEALTH));
                    break;
                case 5:
                    serviceManagement.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.BUSINESS));
                    break;
                case 6:
                    serviceManagement.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.KIDS));
                    break;
                default:
                    console.showMessage("Неверный номер типа услуги.");
            }
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
}
