package org.hotel.constants;

public final class StatusConstants {
    /**
     * Код статуса "успешно".
     */
    public static final int SUCCESS_STATUS = 0;

    /**
     * Код статуса "ошибка транзакции".
     */
    public static final int TRANSACTION_ERROR_STATUS = -3;

    /**
     * Код статуса "занята".
     */
    public static final int OCCUPIED_STATUS = -2;

    /**
     * Код статуса "на обслуживании".
     */
    public static final int IN_SERVICE_STATUS = -1;

    private StatusConstants() { }
}
