package com.example.myapplication;

import java.io.Serializable;

public class WeatherData implements Serializable {
    private String name;
    private String main;
    private String description;
    private double temperatureKelvin;
    private String temperatureCelsius;

    private String windSpeed;

    public WeatherData(String n, String m, String d, String t, String w) {
        name = n;
        main = m;
        description = d;
        temperatureKelvin = Double.parseDouble(t);
        temperatureCelsius = String.format("%.1f" ,temperatureKelvin - 273.15);
        windSpeed = w;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTemperatureKelvin() {
        return temperatureKelvin;
    }

    public void setTemperatureKelvin(double temperatureKelvin) {
        this.temperatureKelvin = temperatureKelvin;
    }
    public String getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(String temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
