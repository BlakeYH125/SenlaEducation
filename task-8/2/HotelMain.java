import injector.Injector;
import model.*;
import controller.*;

public class HotelMain {
    public static void main(String[] args) {
        MainMenuController controller = new MainMenuController();
        Injector.injectDependencies(controller);
        controller.init();
        controller.run();
    }
}