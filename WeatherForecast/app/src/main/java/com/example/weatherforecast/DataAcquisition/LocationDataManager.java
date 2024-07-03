package com.example.weatherforecast.DataAcquisition;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weatherforecast.DataModel.Coords;
import com.example.weatherforecast.DataModel.LocationData;
import com.example.weatherforecast.utils.RetrofitUtil;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationDataManager {

    static LocationService locationService = RetrofitUtil.getClient().create(LocationService.class);
    public static HashMap<String, Coords> locations = new HashMap<String, Coords>();

    final static private String OPENWEATHER_API_KEY = "ae8298f089fc5c51766b0374d72c36c9";

    static public Coords getCoords(String location) {
        if(!locations.containsKey(location)) {
            fetchLocation(location);
        }
        return locations.get(location);
    }

    static public void fetchLocation(String city) {
        locationService.getCoordinates(city, 1, OPENWEATHER_API_KEY).enqueue(new Callback<List<LocationData>>() {
            @Override
            public void onResponse(@NonNull Call<List<LocationData>> call, @NonNull Response<List<LocationData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    LocationData location = response.body().get(0);
                    locations.put(city, new Coords(location.lat, location.lon));
                }
                else {
                    Log.e("WeatherExampleCall", "Failed to fetch coordinates");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LocationData>> call, Throwable t) {
                Log.e("WeatherExampleCall", "Error fetching coordinates", t);
            }
        });
    }
}

