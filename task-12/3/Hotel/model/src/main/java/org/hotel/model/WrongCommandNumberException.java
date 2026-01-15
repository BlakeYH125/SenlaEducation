package org.hotel.model;

public class WrongCommandNumberException extends RuntimeException {
    public WrongCommandNumberException() {
        super("Неверный номер команды.");
    }
}
