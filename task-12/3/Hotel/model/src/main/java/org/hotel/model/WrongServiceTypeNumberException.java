package org.hotel.model;

public class WrongServiceTypeNumberException extends RuntimeException {
    public WrongServiceTypeNumberException() {
        super("Неверный номер типа услуги.");
    }
}
