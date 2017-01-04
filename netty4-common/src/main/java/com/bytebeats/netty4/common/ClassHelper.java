package com.bytebeats.netty4.common;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @date 2016-12-15 11:22
 */
public class ClassHelper {

    public static Class<?> forName(String name) throws ClassNotFoundException {
        return forName(name, getClassLoader());
    }

    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(name, true, classLoader);
    }

    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassHelper.class);
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
