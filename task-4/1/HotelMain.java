import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HotelMain {
    public static void main(String[] args) {
        List<Guest> guests1 = new ArrayList<>();
        List<Guest> guests2 = new ArrayList<>();
        List<Guest> guests3 = new ArrayList<>();

        Administrator administrator = new Administrator();
        Guest guest1 = new Guest("Василий Петров", 35);
        guests1.add(guest1);
        Guest guest2 = new Guest("Василий Щукин", 45);
        guests2.add(guest2);
        Guest guest3 = new Guest("Петр Иванов", 25);
        guests3.add(guest3);

        administrator.addNewRoom(new Room(1, 3000, Status.AVAILABLE, 1, 3)); //проверка добавления комнаты
        administrator.addNewRoom(new Room(2, 4000, Status.AVAILABLE, 2, 3));
        administrator.addNewRoom(new Room(3, 2000, Status.AVAILABLE, 2, 4));
        administrator.addNewRoom(new Room(4, 1500, Status.AVAILABLE, 2, 3));
        administrator.addNewRoom(new Room(5, 1700, Status.AVAILABLE, 2, 4));

        System.out.println(administrator.getRoom(1));
        administrator.settle(1, guests1, 1);
        System.out.println(administrator.getRoom(1).getGuests()); //проверяем гостя у заселенной комнаты
        System.out.println(administrator.getRoom(1));
        administrator.evict(1);
        System.out.println(administrator.getRoom(1).getGuests()); //проверяем что после выселенного гостя нет
        System.out.println();

        administrator.addNewService(new Service("Завтрак", 300, ServiceSection.FOOD)); //проверяем добавление услуг
        administrator.addNewService(new Service("Обед", 500, ServiceSection.FOOD));
        administrator.addNewService(new Service("Услуги парковки", 1000, ServiceSection.PARKING));
        administrator.addNewService(new Service("Бассейн", 300, ServiceSection.HEALTH));
        administrator.addNewService(new Service("Уборка", 150, ServiceSection.CLEANING));
        System.out.println(administrator.getServices().values());
        System.out.println();

        administrator.setInService(2, 3); //проверяем установку статуса на количество дней и смены стоимости
        administrator.setNewRoomPrice(2, 5500);
        administrator.setNewServicePrice("Завтрак", 250);
        System.out.println(administrator.getRoom(2));
        System.out.println(administrator.getService("Завтрак"));
        System.out.println();

        System.out.println("___2 этап___");

        administrator.settle(1, guests2, 3);
        administrator.settle(3, guests1, 5);

        System.out.println(administrator.getAllRoomsWithSort(SortType.PRICE)); //вывод комнат по стоимости
        System.out.println(administrator.getAllRoomsWithSort(SortType.CAPACITY)); //вывод комнат по вместимости
        System.out.println(administrator.getAllRoomsWithSort(SortType.STARS)); //вывод комнат по звездам
        System.out.println();

        System.out.println(administrator.getFreeRoomsWithSort(SortType.PRICE)); //вывод свободных комнат по стоимости
        System.out.println(administrator.getFreeRoomsWithSort(SortType.CAPACITY)); //вывод свободных комнат по вместимости
        System.out.println(administrator.getFreeRoomsWithSort(SortType.STARS)); //вывод свободных комнат по звездам
        System.out.println();

        System.out.println(administrator.getGuestsWithSort(SortType.DATE)); //вывод гостей по дате отбытия
        System.out.println(administrator.getGuestsWithSort(SortType.ALPHABET)); //вывод гостей по алфавиту
        System.out.println();

        System.out.println(administrator.getFreeRoomsCount()); //проверяем количество свободны номеров и гостей в отеле
        System.out.println(administrator.getGuestsCount());
        System.out.println();

        System.out.println(administrator.getFreeRoomsByDate(new Date(System.currentTimeMillis() + 1000))); //проверяем комнаты, которые будут свободны к определенной дате
        System.out.println(administrator.getFreeRoomsByDate(new Date(System.currentTimeMillis() + 1000000000000L)));
        System.out.println();


        System.out.println(administrator.getTotalCost(1));  //проверка подсчета стоимости за комнату
        System.out.println();

        administrator.settle(4, guests1, 1); //проверка вывода трех последних постояльцев комнаты
        administrator.evict(4);
        administrator.settle(4, guests2, 1);
        administrator.evict(4);
        administrator.settle(4, guests3, 1);
        administrator.evict(4);
        System.out.println(administrator.getThreePrevGuests(4));
        System.out.println();

        System.out.println(administrator.getServicesWithSort(SortType.SECTION)); //вывод услуг по разделу
        System.out.println(administrator.getServicesWithSort(SortType.PRICE)); //вывод услуг по цене
        System.out.println();

        System.out.println(administrator.getPriceOfRoomsAndServicesWithSort(SortType.SECTION)); //вывод каталога по разделу
        System.out.println(administrator.getPriceOfRoomsAndServicesWithSort(SortType.PRICE)); //вывод каталога по цене
        System.out.println();

        System.out.println(administrator.getRoomDetails(3)); //проверка вывода деталей о комнате
        System.out.println();
    }
}
