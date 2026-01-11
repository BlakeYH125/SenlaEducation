package injector;

import annotations.Component;
import annotations.Inject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Injector {
    private static final Map<Class<?>, Object> instanceCache = new HashMap<>();

    public static void injectDependencies(Object target) {
        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> dependencyType = field.getType();
                Object dependencyInstance = getComponentInstance(dependencyType);
                try {
                    field.setAccessible(true);
                    field.set(target, dependencyInstance);
                } catch (IllegalAccessException e) {
                    System.err.println("Ошибка внедрения зависимости " + dependencyType.getName() + " в поле " + field.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private static Object getComponentInstance(Class<?> componentType) {
        if (instanceCache.containsKey(componentType)) {
            return instanceCache.get(componentType);
        }

        if (!componentType.isAnnotationPresent(Component.class)) {
            throw new RuntimeException("Класс " + componentType.getName() + " не помечен как @Component и не может быть внедрен.");
        }

        try {
            Object newInstance = componentType.getDeclaredConstructor().newInstance();
            instanceCache.put(componentType, newInstance);
            injectDependencies(newInstance);
            return newInstance;
        } catch (Exception e) {
            instanceCache.remove(componentType);
            throw new RuntimeException("Не удалось создать экземпляр класса " + componentType.getName(), e);
        }
    }
}
