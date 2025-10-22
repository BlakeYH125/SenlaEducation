public class HotelMain {
    public static void main(String[] args) {
        Administrator administrator = new Administrator();
        Guest guest1 = new Guest("Василий Петров", 25);

        administrator.addNewRoom(new Room(1, 3000, "свободна")); //проверка добавления комнаты
        administrator.addNewRoom(new Room(2, 4000, "свободна"));
        administrator.addNewRoom(new Room(3, 5000, "свободна"));

        administrator.settle(1, guest1); //проверка заселения гостя
        System.out.println(administrator.getRooms().get(1));
        System.out.println(administrator.getRooms().get(1).getGuest()); //проверяем гостя у заселенной комнаты
        administrator.evict(1);
        System.out.println(administrator.getRooms().get(1));
        System.out.println(administrator.getRooms().get(1).getGuest()); //проверяем что после выселенного гостя нет
        System.out.println();

        administrator.addNewService(new Service("Завтрак", 300)); //проверяем добавление услуг
        administrator.addNewService(new Service("Обед", 500));
        System.out.println(administrator.getServices());
        System.out.println();

        System.out.println(administrator.getRooms().get(2));
        System.out.println(administrator.getServices().get("Завтрак"));
        administrator.setStatus(2, "обслуживается"); //проверяем установку статуса и смены стоимости
        administrator.setNewRoomPrice(2, 5500);
        administrator.setNewServicePrice("Завтрак", 250);
        System.out.println(administrator.getRooms().get(2));
        System.out.println(administrator.getServices().get("Завтрак"));
    }
}
