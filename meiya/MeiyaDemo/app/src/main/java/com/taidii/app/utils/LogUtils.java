
package com.taidii.app.utils;

import com.google.gson.JsonObject;
import com.orhanobut.logger.Logger;

public class LogUtils {
    static {

    }

    public static void d(String tag, String message) {
            Logger.d(tag, message);

    }

    public static void d(String msg) {
            Logger.d(msg);

    }

    public static void d(String msg, Object... args) {

    }

    public static void d(JsonObject json) {
        d(json.toString());
    }

    public static void d(boolean b) {
        d(b + "");
    }

    public static void d(int i) {
        d(i + "");
    }

    public static void out(String msg) {
            System.out.println(msg);

    }

    public static void e(String tag, String message) {
    }

    public static void trace(String tag, String message) {
    }
}
