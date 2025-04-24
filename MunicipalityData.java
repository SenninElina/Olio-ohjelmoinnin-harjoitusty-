package com.example.myapplication;

import java.io.Serializable;

public class MunicipalityData implements Serializable {

    private int year;
    private int population;
    private int populationChange;
    private double workPlace;
    private double employment;


    public MunicipalityData(int y, int p, int pC) {
        this.year = y;
        this.population = p;
        this.populationChange = pC;
    }


    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getPopulation() {
        return population;
    }
    public void setPopulation(int population) {
        this.population = population;
    }
    public int getPopulationChange() {
        return populationChange;
    }
    public void setPopulationChange(int populationChange) {
        this.populationChange = populationChange;
    }
    public double getWorkPlace() {
        return workPlace;
    }
    public void setWorkPlace(double workPlace) {
        this.workPlace = workPlace;
    }
    public double getEmployment() {
        return employment;
    }
    public void setEmployment(double employment) {
        this.employment = employment;
    }
}
