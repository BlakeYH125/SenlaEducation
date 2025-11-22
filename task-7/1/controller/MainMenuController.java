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
        this.guestController = new GuestController();
        this.roomController = new RoomController();
        this.serviceController = new ServiceController();

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
            console.printMenu();
            int command = console.readInt("Введите номер команды: ");
            switch (command) {
                case 0:
                    DatabaseController.save(administrator.getGuestManagement(), administrator.getRoomManagement(), administrator.getServiceManagement());
                    running = false;
                    break;
                case 1:
                    roomController.evict(console, administrator);
                    break;

                case 2:
                    roomController.settle(console, administrator);
                    break;

                case 3:
                    roomController.setAvailable(console, administrator.getRoomManagement());
                    break;

                case 4:
                    roomController.setOccupied(console, administrator.getRoomManagement());
                    break;

                case 5:
                    roomController.setInService(console, administrator.getRoomManagement());
                    break;

                case 6:
                    roomController.changeRoomPrice(console, administrator.getRoomManagement());
                    break;

                case 7:
                    serviceController.changeServicePrice(console, administrator.getServiceManagement());
                    break;

                case 8:
                    roomController.addRoom(console, administrator.getRoomManagement());
                    break;

                case 9:
                    serviceController.addService(console, administrator.getServiceManagement());
                    break;

                case 10:
                    roomController.showAllRooms(console, administrator.getRoomManagement());
                    break;

                case 11:
                    roomController.showAllFreeRooms(console, administrator.getRoomManagement());
                    break;

                case 12:
                    guestController.showGuests(console, administrator.getGuestManagement());
                    break;

                case 13:
                    roomController.getFreeRoomsCount(console, administrator.getRoomManagement());
                    break;

                case 14:
                    guestController.getGuestsCount(console, administrator.getGuestManagement());
                    break;

                case 15:
                    roomController.showFreeRoomsByDate(console, administrator.getRoomManagement());
                    break;

                case 16:
                    roomController.getTotalCost(console, administrator.getRoomManagement());
                    break;

                case 17:
                    roomController.getThreePrevGuests(console, administrator.getRoomManagement());
                    break;

                case 18:
                    serviceController.showServices(console, administrator.getServiceManagement());
                    break;

                case 19:
                    serviceController.showCatalog(console, administrator);
                    break;

                case 20:
                    roomController.getRoomDetails(console, administrator.getRoomManagement());
                    break;

                case 21:
                    roomController.importRoomData(console, administrator.getRoomManagement());
                    break;

                case 22:
                    roomController.exportRoomData(console, administrator.getRoomManagement());
                    break;

                case 23:
                    serviceController.importServiceData(console, administrator.getServiceManagement());
                    break;

                case 24:
                    serviceController.exportServiceData(console, administrator.getServiceManagement());
                    break;

                case 25:
                    guestController.importServiceData(console, administrator.getGuestManagement());
                    break;

                case 26:
                    guestController.exportServiceData(console, administrator.getGuestManagement());
                    break;

                case 27:
                    guestController.useService(console, administrator);
                    break;

                case 28:
                    guestController.showServicesUsedByGuest(console, administrator);
                    break;

                default:
                    console.showMessage("Введено некорректное значение! Попробуйте снова.");
            }
        }
    }
}
