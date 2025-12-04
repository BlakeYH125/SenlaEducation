package configurator;

import annotations.ConfigProperty;
import annotations.ConfigType;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Configurator {
    private static final Map<String, Properties> propertiesCache = new HashMap<>();

    public static void configure(Object target) {
        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    injectValue(target, field);
                } catch (Exception e) {
                    System.err.println("Ошибка конфигурирования поля " + field.getName() + " в классе " + clazz.getSimpleName() + ": " + e.getMessage());
                }
            }
        }
    }

    private static void injectValue(Object target, Field field) throws Exception {
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String fileName = annotation.configFileName();
        String key = annotation.propertyName().trim();
        Properties props = getProperties(fileName);
        String value = null;
        for (String propertyName : props.stringPropertyNames()) {
            if (propertyName.trim().equals(key)) {
                value = props.getProperty(propertyName);
                break;
            }
        }
        if (value == null) {
            throw new RuntimeException("Ключ '" + key + "' не найден в файле " + fileName);
        }
        Object convertedValue = convert(value.trim(), field.getType(), annotation.type());
        field.setAccessible(true);
        field.set(target, convertedValue);
    }

    private static Properties getProperties(String fileName) throws Exception {
        if (propertiesCache.containsKey(fileName)) {
            return propertiesCache.get(fileName);
        }
        Properties properties = new Properties();

        InputStream input = null;

        try {
            input = new java.io.FileInputStream(fileName);
            System.out.println("Файл с настройками загружен.");
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Файл конфигурации не найден.", e);
        }
        try (InputStream is = input) {
            properties.load(is);
        }

        propertiesCache.put(fileName, properties);
        return properties;
    }

    private static Object convert(String value, Class<?> fieldType, ConfigType explicitType) {
        if (explicitType == ConfigType.STRING || fieldType == String.class) {
            return value;
        }

        if (explicitType == ConfigType.INT || fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        }

        if (explicitType == ConfigType.BOOLEAN || fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }

        return value;
    }
}
