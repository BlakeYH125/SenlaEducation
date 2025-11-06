import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MenuController {
    private Administrator administrator;
    private Console console;
    private boolean running = true;

    public MenuController(Administrator administrator, Console console) {
        this.administrator = administrator;
        this.console = console;
    }

    public void run() {
        int number, daysCount, guestsCount, sortType;
        double price;

        while (running) {
            console.printMenu();
            int command = console.readInt("Введите номер команды: ");
            switch (command) {
                case 0:
                    running = false;
                    break;
                case 1:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    if (!administrator.isFree(number)) {
                        console.showMessage("Комната занята, либо находится на обслуживании.");
                        break;
                    }
                    guestsCount = console.readInt("Введите количестве гостей: ");
                    if (administrator.getRoom(number).getCapacity() < guestsCount) {
                        console.showMessage("Комната расчитана на меньшее количество человек.");
                        break;
                    }
                    List<Guest> guests = new ArrayList<>();
                    for (int i = 0; i < guestsCount; i++) {
                        guests.add(new Guest(console.readString("Введите полное имя: "), console.readInt("Введите возраст: ")));
                    }
                    daysCount = console.readInt("Введите количество дней проживания: ");
                    if (administrator.settle(number, guests, daysCount) == 0) {
                        console.showMessage("Заселение успешно.");
                    } else if (administrator.settle(number, guests, daysCount) == -1) {
                        console.showMessage("Комната на обслуживании.");
                    } else {
                        console.showMessage("Комната занята.");
                    }
                    break;

                case 2:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    if (administrator.isFree(number)) {
                        console.showMessage("Комната никем не занята.");
                        break;
                    }
                    if (administrator.evict(number)) {
                        console.showMessage("Выселение успешно.");
                    } else {
                        console.showMessage("Ошибка при выселении.");
                    }
                    break;

                case 3:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    if (administrator.getRoom(number).getGuests() != null && !administrator.getRoom(number).getGuests().isEmpty()) {
                        console.showMessage("Сначала нужно выселить гостя.");
                    } else if (administrator.isFree(number)) {
                        console.showMessage("Номер уже свободен.");
                    } else {
                        administrator.setAvailable(number);
                        console.showMessage("Установка статуса успешна.");
                    }
                    break;

                case 4:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    if (administrator.isServicing(number)) {
                        console.showMessage("Сначала нужно закончить обслуживание номера.");
                    } else if (administrator.isOccupied(number)) {
                        console.showMessage("Комната уже занята.");
                    } else {
                        daysCount = console.readInt("Введите, на сколько дней установить статус \"занят\": ");
                        administrator.setOccupied(number, daysCount);
                        console.showMessage("Установка статуса успешна.");
                    }
                    break;

                case 5:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    if (administrator.isOccupied(number)) {
                        console.showMessage("Сначала нужно выселить гостя.");
                    } else if (administrator.isServicing(number)) {
                        console.showMessage("Комната уже на обслуживании");
                    } else {
                        daysCount = console.readInt("Введите, на сколько дней установить статус \"на обслуживании\": ");
                        administrator.setInService(number, daysCount);
                        console.showMessage("Установка статуса успешна.");
                    }
                    break;

                case 6:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    price = console.readDouble("Введите новую стоимость номера: ");
                    administrator.setNewRoomPrice(number, price);
                    console.showMessage("Изменение успешно.");
                    break;

                case 7:
                    console.showServices(administrator.getServices());
                    if (administrator.getServices() == null || administrator.getServices().isEmpty()) {
                        break;
                    }
                    String serviceName = console.readString("Введите название услуги: ");
                    Optional<Service> service = administrator.getServices().stream()
                            .filter(s -> s.getName().equalsIgnoreCase(serviceName))
                            .findFirst();
                    if (!service.isPresent()) {
                        console.showMessage("Такой услуги нет.");
                    } else {
                        price = console.readDouble("Введите новую стоимость услуги: ");
                        administrator.setNewServicePrice(serviceName, price);
                        console.showMessage("Изменение успешно.");
                    }
                    break;

                case 8:
                    number = console.readInt("Введите номер комнаты: ");
                    if (administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комната с таким номером уже есть.");
                        break;
                    }
                    administrator.addNewRoom(new Room(number, console.readDouble("Введите суточную стоимость номера: "), Status.AVAILABLE, console.readInt("Введите вместимость номера: "), console.readInt("Введите количество звезд: ")));
                    break;

                case 9:
                    String newServiceName = console.readString("Введите название услуги: ");
                     service = administrator.getServices().stream()
                            .filter(s -> s.getName().equalsIgnoreCase(newServiceName))
                            .findFirst();
                    if (service.isPresent()) {
                        console.showMessage("Такая услуга уже есть.");
                    } else {
                        console.showMessage("1. Питание;\n2. Транспортные услуги;\n3. Уборка;\n4. Здоровье;\n5. Бизнес;\n6. Дети.");
                        int sectionType = console.readInt("Введите номер типа услуги: ");
                        switch (sectionType) {
                            case 1:
                                administrator.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.FOOD));
                                break;
                            case 2:
                                administrator.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.PARKING));
                                break;
                            case 3:
                                administrator.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.CLEANING));
                                break;
                            case 4:
                                administrator.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.HEALTH));
                                break;
                            case 5:
                                administrator.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.BUSINESS));
                                break;
                            case 6:
                                administrator.addNewService(new Service(newServiceName, console.readDouble("Введите стоимость услуги: "), ServiceSection.KIDS));
                                break;
                            default:
                                console.showMessage("Неверный номер типа услуги.");
                        }
                    }
                    break;

                case 10:
                    console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
                    sortType = console.readInt("Выберите вид сортировки: ");
                    if (sortType == 1) {
                        console.showRooms((List<Room>) administrator.getAllRoomsWithSort(SortType.PRICE));
                    } else if (sortType == 2) {
                        console.showRooms((List<Room>) administrator.getAllRoomsWithSort(SortType.CAPACITY));
                    } else if (sortType == 3) {
                        console.showRooms((List<Room>) administrator.getAllRoomsWithSort(SortType.STARS));
                    } else {
                        console.showMessage("Неверный номер команды.");
                    }
                    break;

                case 11:
                    console.showMessage("1. Цена;\n2. Вместимость;\n3. Количество звезд.");
                    sortType = console.readInt("Выберите вид сортировки: ");
                    if (sortType == 1) {
                        console.showRooms((List<Room>) administrator.getFreeRoomsWithSort(SortType.PRICE));
                    } else if (sortType == 2) {
                        console.showRooms((List<Room>) administrator.getFreeRoomsWithSort(SortType.CAPACITY));
                    } else if (sortType == 3) {
                        console.showRooms((List<Room>) administrator.getFreeRoomsWithSort(SortType.STARS));
                    } else {
                        console.showMessage("Неверный номер команды.");
                    }
                    break;

                case 12:
                    console.showMessage("1. Алфавит;\n2. Дата освобождения номера.");
                    sortType = console.readInt("Выберите вид сортировки: ");
                    if (sortType == 1) {
                        console.showGuests((List<Guest>) administrator.getGuestsWithSort(SortType.ALPHABET));
                    } else if (sortType == 2) {
                        console.showGuests((List<Guest>) administrator.getGuestsWithSort(SortType.DATE));
                    } else {
                        console.showMessage("Неверный номер команды.");
                    }
                    break;

                case 13:
                    console.showMessage(String.valueOf(administrator.getFreeRoomsCount()));
                    break;

                case 14:
                    console.showMessage(String.valueOf(administrator.getGuestsCount()));
                    break;

                case 15:
                    daysCount = console.readInt("Введите количество дней от текущей даты: ");
                    console.showRooms(administrator.getFreeRoomsByDate(new Date(System.currentTimeMillis() + daysCount * administrator.getMsecInDay())));
                    break;

                case 16:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    if (administrator.isOccupied(number)) {
                        console.showMessage(String.valueOf(administrator.getTotalCost(number)));
                    } else {
                        console.showMessage("В номере никто не живет.");
                    }
                    break;

                case 17:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    console.showGuests((List<Guest>) administrator.getThreePrevGuests(number));
                    break;

                case 18:
                    console.showMessage("1. Цена;\n2. Раздел.");
                    sortType = console.readInt("Выберите вид сортировки: ");
                    if (sortType == 1) {
                        console.showServices((List<Service>) administrator.getServicesWithSort(SortType.PRICE));
                    } else if (sortType == 2) {
                        console.showServices((List<Service>) administrator.getServicesWithSort(SortType.SECTION));
                    } else {
                        console.showMessage("Неверный номер команды.");
                    }
                    break;

                case 19:
                    console.showMessage("1. Цена;\n2. Раздел.");
                    sortType = console.readInt("Выберите вид сортировки: ");
                    if (sortType == 1) {
                        console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.PRICE));
                    } else if (sortType == 2) {
                        console.showCatalog((List<Priceable>) administrator.getPriceOfRoomsAndServicesWithSort(SortType.SECTION));
                    } else {
                        console.showMessage("Неверный номер команды.");
                    }
                    break;

                case 20:
                    number = console.readInt("Введите номер комнаты: ");
                    if (!administrator.getRooms().containsKey(number)) {
                        console.showMessage("Комнаты с таким номером нет.");
                        break;
                    }
                    console.showMessage(administrator.getRoomDetails(number));
                    break;

                default:
                    console.showMessage("Неверный номер команды.");
            }
        }
    }
}
