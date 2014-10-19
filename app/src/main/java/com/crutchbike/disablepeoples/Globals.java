package com.crutchbike.disablepeoples;

import android.content.SharedPreferences;

import com.loopj.android.http.PersistentCookieStore;


public class Globals {
    public static ApiHTTPConnector HTTPApi;
    public static String Login, Password;
    public static String EmergencyTemplate;
    public static PersistentCookieStore CookManager;
    public static final String StorageName = "com.crutchbike.disablepeoples.storage";
    public static SharedPreferences Settings;

}
