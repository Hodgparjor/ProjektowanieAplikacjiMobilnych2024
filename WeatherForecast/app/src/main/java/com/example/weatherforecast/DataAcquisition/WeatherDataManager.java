package com.example.weatherforecast.DataAcquisition;

import static java.lang.Character.getType;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weatherforecast.DataModel.Coords;
import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.utils.NetworkUtil;
import com.example.weatherforecast.utils.RetrofitUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataManager {
    private Context context;
    private WeatherService weatherService;

    private HashMap<String, WeatherData> weatherDataHashMap = new HashMap<String, WeatherData>();
    private HashMap<String, ForecastData> forecastDataHashMap = new HashMap<String, ForecastData>();

    final private String OPENWEATHER_API_KEY = "ae8298f089fc5c51766b0374d72c36c9";

    public WeatherDataManager(Context context) {
        this.context = context;
        weatherService = RetrofitUtil.getClient().create(WeatherService.class);
    }

    public void fetchData (String location) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            fetchWeatherData(location);
            fetchForecastData(location);
        } else {
            readWeatherDataFromFile();
            readForecastDataFromFile();
        }
    }

    public WeatherData getWeatherData(String location, boolean fetch) {
        if(!weatherDataHashMap.containsKey(location) || fetch) {
            fetchWeatherData(location);
        }
        return weatherDataHashMap.get(location);
    }

    public ForecastData getForecastData(String location, boolean fetch) {
        if(!forecastDataHashMap.containsKey(location) || fetch) {
            fetchForecastData(location);
        }
        return forecastDataHashMap.get(location);
    }

    public void fetchWeatherData(String location) {
        Coords locationCords = LocationDataManager.getCoords(location);
        weatherService.getOpenWeatherWeatherData(locationCords.lat, locationCords.lon, OPENWEATHER_API_KEY).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse( Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData weatherData = response.body();
                    updateWeatherDataToHashMap(location, weatherData);
                    Log.i("fetchWeatherData", "Weather data updated.");
                } else {
                    Log.e("fetchWeatherData","Failed to fetch weather data.");
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.e("fetchWeatherData","Failed to fetch weather data - call failure");
            }
        });
    }

    public void fetchForecastData(String location) {
        Coords locationCords = LocationDataManager.getCoords(location);
        weatherService.getOpenWeatherForecastData(locationCords.lat, locationCords.lon, OPENWEATHER_API_KEY).enqueue(new Callback<ForecastData>() {
            @Override
            public void onResponse(@NonNull Call<ForecastData> call, @NonNull Response<ForecastData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForecastData forecastData = response.body();
                    updateForecastDataToHashMap(location, forecastData);
                } else {
                    Log.e("fetchForecastData","Failed to fetch weather data.");
                }
            }

            @Override
            public void onFailure(Call<ForecastData> call, Throwable t) {
                Log.e("fetchForecastData","Failed to fetch weather data.");
            }
        });
    }

    private void updateWeatherDataToHashMap(String location, WeatherData weatherData) {
        if(weatherDataHashMap.containsKey(location)) {
            weatherDataHashMap.remove(location);
        }
        weatherDataHashMap.put(location, weatherData);
        writeWeatherDataToFile(weatherDataHashMap);
    }

    private void updateForecastDataToHashMap(String location, ForecastData forecastData) {
        if(forecastDataHashMap.containsKey(location)) {
            forecastDataHashMap.remove(location);
        }
        forecastDataHashMap.put(location, forecastData);
        writeForecastDataToFile(forecastDataHashMap);
    }

    private void writeWeatherDataToFile(HashMap<String, WeatherData> weather) {
        try {
            FileOutputStream fos = context.openFileOutput("weather_data.json", Context.MODE_PRIVATE);
            String json = new Gson().toJson(weather);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(e.toString(),e.getMessage());
        }
    }

    private void readWeatherDataFromFile() {
        try {
            FileInputStream fis = context.openFileInput("weather_data.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Type weatherHashMapType = new TypeToken<HashMap<String, WeatherData>>(){}.getType();
            weatherDataHashMap = new Gson().fromJson(sb.toString(), weatherHashMapType);
        } catch (IOException e) {
            Log.e(e.toString(), e.getMessage());
        }
    }

    private void writeForecastDataToFile(HashMap<String, ForecastData> forecast) {
        try {
            FileOutputStream fos = context.openFileOutput("forecast_data.json", Context.MODE_PRIVATE);
            String json = new Gson().toJson(forecast);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(e.toString(),e.getMessage());
        }
    }

    private void readForecastDataFromFile() {
        try {
            FileInputStream fis = context.openFileInput("forecast_data.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Type forecastHashMapType = new TypeToken<HashMap<String, ForecastData>>(){}.getType();
            forecastDataHashMap = new Gson().fromJson(sb.toString(), forecastHashMapType);
        } catch (IOException e) {
            Log.e(e.toString(),e.getMessage());
        }
    }

}
