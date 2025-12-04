package controller;

import annotations.Component;
import model.GuestManagement;
import model.HotelState;
import model.RoomManagement;
import model.ServiceManagement;

import java.io.*;

@Component
public class DatabaseController {
    private static final String FILE_NAME = "database.txt";

    public static void save(GuestManagement guestManagement, RoomManagement roomManagement, ServiceManagement serviceManagement) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            HotelState state = new HotelState(guestManagement, roomManagement, serviceManagement);
            objectOutputStream.writeObject(state);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HotelState load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (HotelState) objectInputStream.readObject();

        } catch (Exception e) {
            return null;
        }
    }
}
