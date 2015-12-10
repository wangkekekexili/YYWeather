package com.yiyangzhu.yyweather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kewang on 12/7/15.
 */
public class Settings {

    public static final String MEASUREMENT_KEY = "measurement";
    public static final String LOCATION_KEY = "city";
    public static final String DEFAULT_MEASUREMENT = "c";
    public static final String DEFAULT_LOCATION = "default";

    public static final String MEASUREMENT_KELVIN = "k";
    public static final String MEASUREMENT_CELSIUS = "c";
    public static final String MEASUREMENT_FAHRENHEIT = "f";

    private Activity activity;
    private String city;
    private String measurement;

    public Settings(Activity activity) {
        this.activity = activity;
    }

    public String getCity() {
        SharedPreferences storage = activity.getSharedPreferences(activity.getPackageName() + ".settings", Context.MODE_PRIVATE);
        city = storage.getString(LOCATION_KEY, DEFAULT_LOCATION);
        return city;
    }

    public String getMeasurement() {
        SharedPreferences storage = activity.getSharedPreferences(activity.getPackageName() + ".settings", Context.MODE_PRIVATE);
        measurement = storage.getString(MEASUREMENT_KEY, DEFAULT_MEASUREMENT);
        return measurement;
    }

    public void setCity(String city) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(activity.getPackageName() + ".settings", Context.MODE_PRIVATE).edit();
        editor.putString(LOCATION_KEY, city);
        editor.apply();
    }

    public void setMeasurement(String measurement) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(activity.getPackageName() + ".settings", Context.MODE_PRIVATE).edit();
        editor.putString(MEASUREMENT_KEY, measurement);
        editor.apply();
    }
}
