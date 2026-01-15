package org.hotel.injector;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.dao.GuestDao;
import org.hotel.dao.RoomDao;
import org.hotel.dao.ServiceDao;
import org.hotel.dao.UsedServiceDao;
import org.hotel.model.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Injector {
    private static final Map<Class<?>, Object> instanceCache = new HashMap<>();
    private static final Map<Class<?>, Class<?>> interfaceToImpl = new HashMap<>();

    static {
        interfaceToImpl.put(GuestRepository.class, GuestDao.class);
        interfaceToImpl.put(RoomRepository.class, RoomDao.class);
        interfaceToImpl.put(ServiceRepository.class, ServiceDao.class);
        interfaceToImpl.put(UsedServiceRepository.class, UsedServiceDao.class);
    }

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
        if (componentType.isInterface()) {
            Class<?> impl = interfaceToImpl.get(componentType);
            if (impl == null) {
                throw new RuntimeException(
                        "Не найдена реализация для интерфейса " + componentType.getName()
                );
            }
            return getComponentInstance(impl);
        }

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
