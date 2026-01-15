package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.model.*;
import org.hotel.view.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public class MainMenuController {
    private static final Logger logger = LogManager.getLogger(MainMenuController.class);

    @Inject
    private Administrator administrator;

    @Inject
    private Console console;

    @Inject
    private GuestController guestController;

    @Inject
    private RoomController roomController;

    @Inject
    private ServiceController serviceController;

    private boolean running = true;

    public MainMenuController() {};

    public void init() {
        guestController.init();
        roomController.init();
        serviceController.init();
    }

    public void run() {
        try {
            logger.info("Начало выполнения программы");
            while (running) {
                console.printMainMenu();
                int command = console.readInt("Введите номер команды: ");
                switch (command) {
                    case 0:
                        running = false;
                        break;
                    case 1:
                        roomController.run();
                        break;

                    case 2:
                        serviceController.run();
                        break;

                    case 3:
                        guestController.run();
                        break;

                    default:
                        console.showMessage("Введено некорректное значение! Попробуйте снова.");
                }
            }
            logger.info("Программа успешно завершила работу");
        } catch (Exception e) {
            logger.error("Ошибка при выполнении главной программы" + e.getMessage());
        }
    }
}
