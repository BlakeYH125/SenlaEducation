package org.hotel.model;

public enum Status {
    /**
     * Статус свободный.
     */
    AVAILABLE("свободный"),

    /**
     * Статус занят.
     */
    OCCUPIED("занят"),

    /**
     * Статус обслуживается.
     */
    IN_SERVICE("обслуживается");

    /**
     * Название.
     */
    private final String label;

    Status(final String labelP) {
        this.label = labelP;
    }

    @Override
    public String toString() {
        return label;
    }
}
