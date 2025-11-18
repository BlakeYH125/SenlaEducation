package view;

import model.*;

import java.util.List;
import java.util.Scanner;

public class Console {
    private final Scanner scanner = new Scanner(System.in);

    public int readInt(String message) {
        System.out.print(message);
        while (true) {
            try {
                int number = Integer.parseInt(scanner.nextLine());
                if (number >= 0) {
                    return number;
                } else {
                    System.out.println("Введено некорректное значение! Попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }

    public double readDouble(String message) {
        System.out.print(message);
        while (true) {
            try {
                double number = Integer.parseInt(scanner.nextLine());
                if (number >= 0) {
                    return number;
                } else {
                    System.out.println("Введено некорректное значение! Попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }

    public String readString(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void printMenu() {
        System.out.println("\n0. Выход;");
        System.out.println("1. Заселить в номер;");
        System.out.println("2. Выселить из номера;");
        System.out.println("3. Изменить статус номера на \"свободный\";");
        System.out.println("4. Изменить статус номера на \"занят\";");
        System.out.println("5. Изменить статус номера на \"на обслуживании\";");
        System.out.println("6. Изменить цену номера;");
        System.out.println("7. Изменить цену услуги;");
        System.out.println("8. Добавить номер;");
        System.out.println("9. Добавить услугу;");
        System.out.println("10. Просмотреть список всех номеров;");
        System.out.println("11. Просмотреть список всех свободных номеров;");
        System.out.println("12. Просмотреть список постояльцев и их номеров;");
        System.out.println("13. Просмотреть общее число всех свободных номеров;");
        System.out.println("14. Просмотреть общее число постояльцев;");
        System.out.println("15. Просмотреть список номеров, которые будут свободны к определенной дате;");
        System.out.println("16. Просмотреть сумму, которую должен оплатить постоялец за номер;");
        System.out.println("17. Посмотреть 3-х последних постояльцев номера и даты их пребывания;");
        System.out.println("18. Просмотреть список услуг и их цену;");
        System.out.println("19. Просмотреть список цен на услуги и номера;");
        System.out.println("20. Посмотреть детали отдельного номера.");
        System.out.println("21. Сделать импорт данных о комнате(ах).");
        System.out.println("22. Сделать экспорт данных о комнате.");
        System.out.println("23. Сделать импорт данных о услуге(ах).");
        System.out.println("24. Сделать экспорт данных о услуге.");
        System.out.println("25. Сделать импорт данных об госте(ях).");
        System.out.println("26. Сделать экспорт данных об госте.\n");
    }

    public void showRooms(List<Room> rooms) {
        if (rooms == null || rooms.isEmpty()) {
            System.out.println("Список комнат пуст.");
        } else {
            for (Room room : rooms) {
                System.out.println(room);
            }
        }
    }

    public void showGuests(List<Guest> guests) {
        if (guests == null || guests.isEmpty()) {
            System.out.println("Список постояльцев пуст.");
        } else {
            for (Guest guest : guests) {
                System.out.println(guest);
            }
        }
    }

    public void showCatalog(List<Priceable> catalog) {
        if (catalog == null || catalog.isEmpty()) {
            System.out.println("Каталог пуст.");
        } else {
            for (Priceable price : catalog) {
                System.out.println(price);
            }
        }
    }

    public void showServices(List<Service> services) {
        if (services == null || services.isEmpty()) {
            System.out.println("Список услуг пуст.");
        } else {
            for (Service service : services) {
                System.out.println(service);
            }
        }
    }
}
