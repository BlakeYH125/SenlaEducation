package model;

import java.util.Properties;

public class PropertySettings {
    private static boolean isAllowChange;
    private static int previousGuestsLimit;

    PropertySettings(Properties properties) {
        this.isAllowChange = Boolean.parseBoolean(properties.getProperty("hotel.room.status.changing"));
        this.previousGuestsLimit = Integer.parseInt(properties.getProperty("hotel.room.history.limit"));
    }

    public static boolean isIsAllowChange() {
        return isAllowChange;
    }

    public static int getPreviousGuestsLimit() {
        return previousGuestsLimit;
    }
}
