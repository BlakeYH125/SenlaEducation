package org.hotel.app;

import org.hotel.controller.MainMenuController;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class App {
    private App() { }

    public static void main(final String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        MainMenuController controller = applicationContext.getBean(MainMenuController.class);
        controller.run();
        applicationContext.close();
    }
}
