package com.yiyangzhu.yyweather;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a city's weather.
 */
public class CityWeather {

    private String name;
    private String weather;
    private double temperature;
    private List<String> forecastWeather = new ArrayList<>();
    private List<Double> forecastTemperature = new ArrayList<>();

    /**
     * CityWeather object should be instantiated through the builder class.
     */
    private CityWeather() {
    }

    public static class Builder {

        private CityWeather instance;

        public Builder() {
            instance = new CityWeather();
        }

        public Builder setName(String city) {
            instance.name = city;
            return this;
        }

        public Builder setWeather(String weather) {
            instance.weather = weather;
            return this;
        }

        public Builder setTemperature(double temperature) {
            instance.temperature = temperature;
            return this;
        }

        public CityWeather build() {
            return instance;
        }

    }

    public void addForecastWeather(String forecastWeather) {
        this.forecastWeather.add(forecastWeather);
    }

    public void addForecastWeather(double forecastTemperature) {
        this.forecastTemperature.add(forecastTemperature);
    }

    public String getName() {
        return name;
    }

    public String getWeather() {
        return weather;
    }

    public double getTemperature() {
        return temperature;
    }

    public List<String> getForecastWeather() {
        return forecastWeather;
    }

    public List<Double> getForecastTemperature() {
        return forecastTemperature;
    }
}
