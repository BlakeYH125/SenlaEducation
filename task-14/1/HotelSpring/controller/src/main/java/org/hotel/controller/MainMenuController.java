package org.hotel.controller;

import org.hotel.constants.CommandConstants;
import org.hotel.model.management.Administrator;
import org.hotel.view.Console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public final class MainMenuController {
    /**
     * Логгер для фиксации логов.
     */
    private static final Logger LOGGER = LogManager.getLogger(MainMenuController.class);

    /**
     * Администратор.
     */
    private final Administrator administrator;

    /**
     * Вывод на консоль.
     */
    private final Console console;

    /**
     * Управление гостями.
     */
    private final GuestController guestController;

    /**
     * Управление комнатами.
     */
    private final RoomController roomController;

    /**
     * Управление услугами.
     */
    private final ServiceController serviceController;

    /**
     * Индикатор работы.
     */
    private boolean running = true;

    public MainMenuController(final Administrator administratorP, final Console consoleP, final GuestController guestControllerP, final RoomController roomControllerP, final ServiceController serviceControllerP) {
        this.administrator = administratorP;
        this.console = consoleP;
        this.guestController = guestControllerP;
        this.roomController = roomControllerP;
        this.serviceController = serviceControllerP;
    };

    public void init() {
        guestController.init();
        roomController.init();
        serviceController.init();
    }

    public void run() {
        try {
            LOGGER.info("Начало выполнения программы");
            while (running) {
                console.printMainMenu();
                int command = console.readInt("Введите номер команды: ");
                switch (command) {
                    case CommandConstants.COMMAND_ZERO:
                        running = false;
                        break;
                    case CommandConstants.COMMAND_ONE:
                        roomController.run();
                        break;

                    case CommandConstants.COMMAND_TWO:
                        serviceController.run();
                        break;

                    case CommandConstants.COMMAND_THREE:
                        guestController.run();
                        break;

                    default:
                        console.showMessage("Введено некорректное значение! Попробуйте снова.");
                }
            }
            LOGGER.info("Программа успешно завершила работу");
        } catch (Exception e) {
            LOGGER.error("Ошибка при выполнении главной программы" + e.getMessage());
        }
    }
}
