package com.example.myapplication;

public class CoronaData {
    private String location;
    private int deaths;

    public CoronaData(String location, int deaths) {
        this.location = location;
        this.deaths = deaths;
    }

    public String getLocation() {
        return location;
    }

    public int getDeaths() {
        return deaths;
    }

}