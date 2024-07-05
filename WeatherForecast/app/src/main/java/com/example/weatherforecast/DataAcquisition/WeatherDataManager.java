package com.example.weatherforecast.DataAcquisition;

import static java.lang.Character.getType;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.LocationData;
import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.UI.WeatherViewModel;
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
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDataManager {
    private Context context;
    private WeatherService weatherService;

    private WeatherViewModel vm;

    private HashMap<String, WeatherData> weatherDataHashMap = new HashMap<String, WeatherData>();
    private HashMap<String, ForecastData> forecastDataHashMap = new HashMap<String, ForecastData>();

    private HashSet<String> savedCities = new HashSet<String>();

    final private String OPENWEATHER_API_KEY = "ae8298f089fc5c51766b0374d72c36c9";

    public WeatherDataManager(Context context, WeatherViewModel vm) {
        this.context = context;
        this.vm = vm;
        weatherService = RetrofitUtil.getClient().create(WeatherService.class);
        readSavedCitiesFromFile();
    }

    public void fetchData (String location) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            Log.i("WDM_FetchData", "Fetching forecast and weather for " + location + "...");
            fetchForecastAndWeather(location);
        } else {
            readWeatherDataFromFile();
            setWeatherDataFromHashMap(location);
            readForecastDataFromFile();
            setForecastDataHashMap(location);
        }
    }

    public WeatherData getWeatherData(String location, boolean fetch) {
        if(!weatherDataHashMap.containsKey(location) || fetch) {
            fetchForecastAndWeather(location);
        }
        return weatherDataHashMap.get(location);
    }

    public ForecastData getForecastData(String location, boolean fetch) {
        if(!forecastDataHashMap.containsKey(location) || fetch) {
            fetchForecastAndWeather(location);
        }
        return forecastDataHashMap.get(location);
    }

     public void fetchForecastAndWeather(String city) {
        weatherService.getCoordinates(city, 1, OPENWEATHER_API_KEY).enqueue(new Callback<List<LocationData>>() {
            @Override
            public void onResponse( Call<List<LocationData>> call, Response<List<LocationData>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    LocationData location = response.body().get(0);
                    Log.i("LocationCall", "Successfully fetched lat: " + location.lat + " and lon: " + location.lon + " for " + city);
                    fetchForecastData(city, location);
                    fetchWeatherData(city, location);
                }
                else {
                    Log.e("LocationCall", "Failed to fetch coordinates");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<LocationData>> call, Throwable t) {
                Log.e("LocationCall", "Error fetching coordinates", t);
            }
        });
    }

    public void fetchWeatherData(String city, LocationData location) {
        weatherService.getOpenWeatherWeatherData(location.lat, location.lon, OPENWEATHER_API_KEY).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse( Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData weatherData = response.body();
                    vm.setWeatherData(weatherData);
                    updateWeatherDataToHashMap(city, weatherData);
                    Log.i("fetchWeatherData", "Weather data updated for " + city + ". Timestamp: " + weatherData.dt + " weather.name: " + weatherData.name);
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

    public void fetchForecastData(String city, LocationData location) {
        weatherService.getOpenWeatherForecastData(location.lat, location.lon, OPENWEATHER_API_KEY).enqueue(new Callback<ForecastData>() {
            @Override
            public void onResponse(@NonNull Call<ForecastData> call, @NonNull Response<ForecastData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForecastData forecastData = response.body();
                    vm.setForecastData(forecastData);
                    Log.i("fetchForecastData", "Forecast data updated for " + city);
                    updateForecastDataToHashMap(city, forecastData);
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
        if(!savedCities.contains(location)) {
            return;
        }
        if(weatherDataHashMap.containsKey(location)) {
            weatherDataHashMap.remove(location);
        }
        weatherDataHashMap.put(location, weatherData);
        writeWeatherDataToFile(weatherDataHashMap);
    }

    private void updateForecastDataToHashMap(String location, ForecastData forecastData) {
        if(!savedCities.contains(location)) {
            return;
        }
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

    private void saveSavedCitiesToFile(HashSet<String> cities) {
        try {
            FileOutputStream fos = context.openFileOutput("saved_cities.json", Context.MODE_PRIVATE);
            String json = new Gson().toJson(cities);
            fos.write(json.getBytes());
            fos.close();
        } catch (IOException e) {
            Log.e(e.toString(),e.getMessage());
        }
    }

    private void readSavedCitiesFromFile() {
        try {
            FileInputStream fis = context.openFileInput("saved_cities.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Type savedCitiesHashSetType = new TypeToken<HashSet<String>>(){}.getType();
            savedCities = new Gson().fromJson(sb.toString(), savedCitiesHashSetType);
        } catch (IOException e) {
            Log.e(e.toString(), e.getMessage());
        }
    }


    private void setWeatherDataFromHashMap(String city) {
        if(weatherDataHashMap.containsKey(city)) {
            vm.setWeatherData(weatherDataHashMap.get(city));
        }
    }

    public void addToSavedCities(String city) {
        savedCities.add(city);
        fetchForecastAndWeather(city);
        saveSavedCitiesToFile(savedCities);
    }

    public void removeFromSavedCities(String city) {
        savedCities.remove(city);
        weatherDataHashMap.remove(city);
        forecastDataHashMap.remove(city);
        writeForecastDataToFile(forecastDataHashMap);
        writeWeatherDataToFile(weatherDataHashMap);
        saveSavedCitiesToFile(savedCities);
    }

    public boolean isCitySaved(String city) {
        readSavedCitiesFromFile();
        return savedCities.contains(city);
    }

    public HashSet<String> getSavedCities() {
        return savedCities;
    }
    private void setForecastDataHashMap(String city) {
        if(forecastDataHashMap.containsKey(city)) {
            vm.setForecastData(forecastDataHashMap.get(city));
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
