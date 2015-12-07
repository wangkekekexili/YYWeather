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
        SharedPreferences storage = activity.getSharedPreferences(activity.getPackageName() + ".settings", Context.MODE_PRIVATE);
        city = storage.getString(LOCATION_KEY, DEFAULT_LOCATION);
        measurement = storage.getString(MEASUREMENT_KEY, DEFAULT_MEASUREMENT);
    }

    public String getCity() {
        return city;
    }

    public String getMeasurement() {
        return measurement;
    }
}
