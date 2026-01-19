package org.hotel.controller;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.constants.CommandConstants;
import org.hotel.model.Administrator;
import org.hotel.view.Console;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Component
public final class MainMenuController {
    /**
     * Логгер для фиксации логов.
     */
    private static final Logger LOGGER = LogManager.getLogger(MainMenuController.class);

    /**
     * Администратор.
     */
    @Inject
    private Administrator administrator;

    /**
     * Вывод на консоль.
     */
    @Inject
    private Console console;

    /**
     * Управление гостями.
     */
    @Inject
    private GuestController guestController;

    /**
     * Управление комнатами.
     */
    @Inject
    private RoomController roomController;

    /**
     * Управление услугами.
     */
    @Inject
    private ServiceController serviceController;

    /**
     * Индикатор работы.
     */
    private boolean running = true;

    public MainMenuController() { };

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
