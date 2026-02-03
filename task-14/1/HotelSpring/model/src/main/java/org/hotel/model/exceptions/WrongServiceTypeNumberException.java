package org.hotel.model.exceptions;

public class WrongServiceTypeNumberException extends RuntimeException {
    public WrongServiceTypeNumberException() {
        super("Неверный номер типа услуги.");
    }
}
