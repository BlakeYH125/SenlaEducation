import model.*;
import view.Console;
import controller.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class HotelMain {
    public static void main(String[] args) {
        Properties settings = new Properties();
        try {
            settings.load(Files.newBufferedReader(Paths.get("settings.properties")));
        } catch (Exception e) {
            System.out.println("Ошибка загрузки настроек.");
        }
        Administrator administrator = new Administrator(settings);
        Console console = new Console();
        MainMenuController controller = new MainMenuController(administrator, console);
        controller.run();
    }
}