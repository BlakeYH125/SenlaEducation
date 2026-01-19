package org.hotel.model;

public class GuestNotFoundException extends RuntimeException {
    public GuestNotFoundException() {
        super("Гостя с таким id нет.");
    }
}
