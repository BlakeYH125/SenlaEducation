package org.hotel.model;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException() {
        super("Услуги с таким id нет.");
    }
}
