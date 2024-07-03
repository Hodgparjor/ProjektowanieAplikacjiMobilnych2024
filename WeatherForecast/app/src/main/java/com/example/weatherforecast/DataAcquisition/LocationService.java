package com.example.weatherforecast.DataAcquisition;

import com.example.weatherforecast.DataModel.LocationData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {
    @GET("geo/1.0/direct")
    Call<List<LocationData>> getCoordinates(@Query("q") String city, @Query("limit") int limit, @Query("appid") String apiKey);
}
