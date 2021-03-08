package com.example.multidex;

import java.lang.reflect.Field;

public class ShareReflectUtil {

    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        throw new NoSuchFieldException("Field" + name + " not found in " + instance.getClass());
    }
}

