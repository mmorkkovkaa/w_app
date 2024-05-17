package com.example.weatherapi.remote_data;


import com.example.weatherapi.models.Model;
import com.example.weatherapi.models.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("/data/2.5/weather")
    Call<Model> getCurrentWeather(
            @Query("q")String name,
            @Query("appid")String key);

    @GET("/data/2.5/weather?&appid=6d2d2dfe05a3c067210b86ca31aa3440")
    Call<WeatherModel> getWeather(
            @Query("q")String name,
            @Query("appid")String key
    );
//   public static final String url="api.openweathermap.org/data/2.5/weather?q=London&appid=6d2d2dfe05a3c067210b86ca31aa3440";
    public static final String url="6d2d2dfe05a3c067210b86ca31aa3440";


}
