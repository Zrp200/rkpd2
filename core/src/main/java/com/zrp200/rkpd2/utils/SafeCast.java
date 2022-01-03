package com.zrp200.rkpd2.utils;

// I got sick of instanceof and casting on seperate lines.
public class SafeCast {
    /** casts if safe, returns null if invalid.
     *
     * Shortcut for using instanceof operator and then casting, though it's probably less efficient.
     * **/
    public static <T> T cast(Object o, Class<T> cls) {
        return cls.isInstance(o) ? cls.cast(o) : null;
    }

    private SafeCast() {} // singleton.
}
