package com.example.weatherapi.models;

import com.google.gson.annotations.SerializedName;

public class Model {
    @SerializedName("main")
    Main model;

    @SerializedName("wind")
    Wind wind_model;

    @SerializedName("sys")
    Sys sys_model;

    @SerializedName("clouds")
    Clouds clouds_model;
    @SerializedName("timezone")
    long timezone;

    public long getTimezone() {
        return timezone;
    }

    public Main getModel() {
        return model;
    }

    public Wind getWind_model() {
        return wind_model;
    }

    public Sys getSys_model() {
        return sys_model;
    }

    public Clouds getClouds_model() {
        return clouds_model;
    }

}
