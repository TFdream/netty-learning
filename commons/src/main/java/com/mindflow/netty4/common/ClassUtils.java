package com.mindflow.netty4.common;

/**
 *
 * @author Ricky Fung
 */
public abstract class ClassUtils {

    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name, getClassLoader());
    }

    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(name, true, classLoader);
    }

    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassUtils.class);
    }

    public static ClassLoader getClassLoader(Class<?> cls) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = cls.getClassLoader();
        }
        return cl;
    }

}
