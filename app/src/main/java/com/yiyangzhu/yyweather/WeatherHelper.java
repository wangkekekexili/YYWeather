package com.yiyangzhu.yyweather;

/**
 * WeatherHelper deals with different format of weather.
 */
public class WeatherHelper {

    public static double k2c(double t) {
        return t - 273.15;
    }

    public static double k2f(double t) {
        return (t - 273.15) * 1.8 + 32;
    }

}
