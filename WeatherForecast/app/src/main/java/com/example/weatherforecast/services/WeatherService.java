package com.example.weatherforecast.services;

import android.content.Context;

import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.utils.NetworkUtil;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherService {
    private Context context;

    public WeatherService(Context context) {
        this.context = context;
    }

    public void getData(String location) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            // Make network request to OpenWeatherMap API
            // Parse response and save to database
        } else {
            // Load data from database
        }
    }

    @GET("data/2.5/weather")
    Call<WeatherData> getWeatherData(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String apiKey) {

        return null;
    }

    @GET("data/2.5/forecast")
    Call<ForecastData> getForecastData(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String apiKey) {

        return null;
    }
}
