package com.twopibd.dactarbari.ambulance.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchedPlaceModel {

    @SerializedName("predictions")
    @Expose
    private List<Prediction> predictions = null;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

}
