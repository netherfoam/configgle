package org.maxgamer.configgle.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author netherfoam
 */
public class TypeUtil {
    private static final Map<Class<?>, Class<?>> TYPES = new HashMap<>();

    static {
        TYPES.put(long.class, Long.class);
        TYPES.put(int.class, Integer.class);
        TYPES.put(short.class, Short.class);
        TYPES.put(byte.class, Byte.class);
        TYPES.put(double.class, Double.class);
        TYPES.put(float.class, Float.class);
        TYPES.put(char.class, Character.class);
        TYPES.put(boolean.class, Boolean.class);
    }


    public static Class<?> box(Class<?> primitive) {
        if (!primitive.isPrimitive()) throw new IllegalArgumentException(primitive + " is not a primitive type");

        Class<?> boxed = TYPES.get(primitive);

        if(boxed == null) {
            throw new IllegalStateException("Unhandled primitive type: " + primitive);
        }

        return boxed;
    }
}
