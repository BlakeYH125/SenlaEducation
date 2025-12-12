package model;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException() {
        super("Комнаты с таким id нет.");
    }
}
