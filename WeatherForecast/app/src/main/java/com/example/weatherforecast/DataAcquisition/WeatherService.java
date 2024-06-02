package com.example.weatherforecast.DataAcquisition;

import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("data/2.5/weather")
    Call<WeatherData> getOpenWeatherWeatherData(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String apiKey);
    @GET("data/2.5/forecast")
    Call<ForecastData> getOpenWeatherForecastData(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String apiKey);
}
