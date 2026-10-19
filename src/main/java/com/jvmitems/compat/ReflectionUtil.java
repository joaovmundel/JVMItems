package com.jvmitems.compat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class ReflectionUtil {

    private ReflectionUtil() {
    }

    static Class<?> craftClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + VersionUtil.nmsVersion() + "." + name);
    }

    static Class<?> nmsClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + VersionUtil.nmsVersion() + "." + name);
    }

    static Object invokeStatic(Class<?> clazz, String method, Class<?>[] types, Object... args) throws Exception {
        Method m = clazz.getMethod(method, types);
        return m.invoke(null, args);
    }

    static Object invoke(Object target, String method) throws Exception {
        Method m = target.getClass().getMethod(method);
        return m.invoke(target);
    }

    static Object invoke(Object target, String method, Class<?>[] types, Object... args) throws Exception {
        Method m = findMethod(target.getClass(), method, types);
        return m.invoke(target, args);
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            for (Method m : current.getDeclaredMethods()) {
                if (!m.getName().equals(name) || m.getParameterTypes().length != types.length) {
                    continue;
                }
                boolean matches = true;
                Class<?>[] params = m.getParameterTypes();
                for (int i = 0; i < params.length; i++) {
                    if (!params[i].isAssignableFrom(types[i]) && !params[i].equals(types[i])) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    m.setAccessible(true);
                    return m;
                }
            }
            current = current.getSuperclass();
        }
        throw new NoSuchMethodException(name);
    }

    static Object newInstance(Class<?> clazz) throws Exception {
        Constructor<?> c = clazz.getConstructor();
        c.setAccessible(true);
        return c.newInstance();
    }
}
