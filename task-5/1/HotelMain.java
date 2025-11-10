import education.task5.model.*;
import education.task5.view.Console;
import education.task5.controller.*;

public class HotelMain {
    public static void main(String[] args) {
        Administrator administrator = new Administrator();
        Console console = new Console();
        MainMenuController controller = new MainMenuController(administrator, console);
        controller.run();
    }
}