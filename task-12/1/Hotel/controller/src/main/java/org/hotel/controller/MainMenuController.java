package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.model.*;
import org.hotel.view.*;

@Component
public class MainMenuController {
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
    }
}
