package com.twopibd.dactarbari.ambulance.model;

public class AmbulanceCostType {
    String name;
    AmbulanceTypeCostFB ambulanceTypeCostFB;

    public AmbulanceCostType() {
    }

    public AmbulanceCostType(String name, AmbulanceTypeCostFB ambulanceTypeCostFB) {
        this.name = name;
        this.ambulanceTypeCostFB = ambulanceTypeCostFB;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AmbulanceTypeCostFB getAmbulanceTypeCostFB() {
        return ambulanceTypeCostFB;
    }

    public void setAmbulanceTypeCostFB(AmbulanceTypeCostFB ambulanceTypeCostFB) {
        this.ambulanceTypeCostFB = ambulanceTypeCostFB;
    }
}
