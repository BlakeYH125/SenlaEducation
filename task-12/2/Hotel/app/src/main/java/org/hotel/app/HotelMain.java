package org.hotel.app;

import org.hotel.injector.Injector;
import org.hotel.model.*;
import org.hotel.controller.*;

public class HotelMain {
    public static void main(String[] args) {
        MainMenuController controller = new MainMenuController();
        Injector.injectDependencies(controller);
        controller.init();
        controller.run();
    }
}
