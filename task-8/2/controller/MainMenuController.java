package controller;

import annotations.Component;
import annotations.Inject;
import model.*;
import view.*;

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
        HotelState state = DatabaseController.load();
        if (state != null) {
            administrator.setGuestManagement(state.getGuestManagement());
            administrator.setRoomManagement(state.getRoomManagement());
            administrator.setServiceManagement(state.getServiceManagement());
        }

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
                    DatabaseController.save(administrator.getGuestManagement(), administrator.getRoomManagement(), administrator.getServiceManagement());
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
