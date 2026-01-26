package org.hotel.injector;

import org.hotel.annotations.Component;
import org.hotel.annotations.Inject;
import org.hotel.dao.GuestDao;
import org.hotel.dao.RoomDao;
import org.hotel.dao.ServiceDao;
import org.hotel.dao.UsedServiceDao;
import org.hotel.model.repository.GuestRepository;
import org.hotel.model.repository.RoomRepository;
import org.hotel.model.repository.ServiceRepository;
import org.hotel.model.repository.UsedServiceRepository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class Injector {
    /**
     * Уже созданные экземпляры классов.
     */
    private static final Map<Class<?>, Object> INSTANCE_CACHE = new HashMap<>();

    /**
     * Интерфейсы и классы которые нужно внедрить вместо них.
     */
    private static final Map<Class<?>, Class<?>> INTERFACE_TO_IMLP = new HashMap<>();

    static {
        INTERFACE_TO_IMLP.put(GuestRepository.class, GuestDao.class);
        INTERFACE_TO_IMLP.put(RoomRepository.class, RoomDao.class);
        INTERFACE_TO_IMLP.put(ServiceRepository.class, ServiceDao.class);
        INTERFACE_TO_IMLP.put(UsedServiceRepository.class, UsedServiceDao.class);
    }

    private Injector() { }

    public static void injectDependencies(final Object targetP) {
        Class<?> clazz = targetP.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                Class<?> dependencyType = field.getType();
                Object dependencyInstance = getComponentInstance(dependencyType);
                try {
                    field.setAccessible(true);
                    field.set(targetP, dependencyInstance);
                } catch (IllegalAccessException e) {
                    System.err.println("Ошибка внедрения зависимости " + dependencyType.getName() + " в поле " + field.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    private static Object getComponentInstance(final Class<?> componentTypeP) {
        if (componentTypeP.isInterface()) {
            Class<?> impl = INTERFACE_TO_IMLP.get(componentTypeP);
            if (impl == null) {
                throw new RuntimeException(
                        "Не найдена реализация для интерфейса " + componentTypeP.getName()
                );
            }
            return getComponentInstance(impl);
        }

        if (INSTANCE_CACHE.containsKey(componentTypeP)) {
            return INSTANCE_CACHE.get(componentTypeP);
        }

        if (!componentTypeP.isAnnotationPresent(Component.class)) {
            throw new RuntimeException("Класс " + componentTypeP.getName() + " не помечен как @Component и не может быть внедрен.");
        }

        try {
            Object newInstance = componentTypeP.getDeclaredConstructor().newInstance();
            INSTANCE_CACHE.put(componentTypeP, newInstance);
            injectDependencies(newInstance);
            return newInstance;
        } catch (Exception e) {
            INSTANCE_CACHE.remove(componentTypeP);
            throw new RuntimeException("Не удалось создать экземпляр класса " + componentTypeP.getName(), e);
        }
    }
}
