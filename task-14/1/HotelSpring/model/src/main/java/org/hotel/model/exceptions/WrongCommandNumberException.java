package org.hotel.model.exceptions;

public class WrongCommandNumberException extends RuntimeException {
    public WrongCommandNumberException() {
        super("Неверный номер команды.");
    }
}
