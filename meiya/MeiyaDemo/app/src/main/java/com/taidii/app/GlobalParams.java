package com.taidii.app;

import android.graphics.Typeface;
import android.support.v4.util.ArrayMap;


import java.util.ArrayList;

public class GlobalParams {
    public static float deviceDensity;
    public static int devicePixelsHeight;
    public static String token;
    public static String username;
    public static String password;
    public static boolean canReply;
    public static boolean canShare;
    public static boolean isProfileChanged = false;
    public static Typeface FONT_BLOB;
    public static boolean isPaySuccess = false;
    public static boolean isCreatedOrder = false;
    public static Typeface FONT_REGULAR;
    public static Typeface FONT_LIGHT;
    public static ArrayList<String> schoolMenuTags = new ArrayList<>();
    public static boolean canDownload = false;
    public static ArrayMap<Long, Integer> UnreadCounts = new ArrayMap<>();

}
