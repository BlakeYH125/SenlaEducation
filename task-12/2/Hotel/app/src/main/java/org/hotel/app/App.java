package org.hotel.app;

import org.hotel.injector.Injector;
import org.hotel.controller.MainMenuController;

public final class App {
    private App() { }

    public static void main(final String[] args) {
        MainMenuController controller = new MainMenuController();
        Injector.injectDependencies(controller);
        controller.init();
        controller.run();
    }
}
