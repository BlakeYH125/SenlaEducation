package controller;

import model.*;
import view.*;

public class MainMenuController {
    private GuestController guestController;
    private RoomController roomController;
    private ServiceController serviceController;
    private Administrator administrator;
    private Console console;
    private boolean running = true;

    public MainMenuController(Administrator administrator, Console console) {
        this.console = console;
        this.guestController = new GuestController(administrator, console);
        this.roomController = new RoomController(administrator, console);
        this.serviceController = new ServiceController(administrator, console);

        HotelState state = DatabaseController.load();
        if (state != null) {
            administrator.setGuestManagement(state.getGuestManagement());
            administrator.setRoomManagement(state.getRoomManagement());
            administrator.setServiceManagement(state.getServiceManagement());
        }
        this.administrator = administrator;
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
