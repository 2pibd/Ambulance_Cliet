package com.twopibd.dactarbari.ambulance.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddtionalFeature {

    @SerializedName("Doctor")
    @Expose
    private Long doctor;
    @SerializedName("Nurse")
    @Expose
    private Long nurse;

    public Long getDoctor() {
        return doctor;
    }

    public void setDoctor(Long doctor) {
        this.doctor = doctor;
    }

    public Long getNurse() {
        return nurse;
    }

    public void setNurse(Long nurse) {
        this.nurse = nurse;
    }

}