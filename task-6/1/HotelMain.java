import model.*;
import view.Console;
import controller.*;

public class HotelMain {
    public static void main(String[] args) {
        Administrator administrator = new Administrator();
        Console console = new Console();
        MainMenuController controller = new MainMenuController(administrator, console);
        controller.run();
    }
}