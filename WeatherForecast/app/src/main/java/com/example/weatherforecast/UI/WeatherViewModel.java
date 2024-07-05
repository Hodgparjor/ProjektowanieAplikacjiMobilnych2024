package com.example.weatherforecast.UI;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecast.DataAcquisition.WeatherDataManager;
import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;

import java.io.Serializable;
import java.util.HashSet;

public class WeatherViewModel extends AndroidViewModel implements Serializable {
    private final MutableLiveData<WeatherData> weatherData = new MutableLiveData<>();
    private final MutableLiveData<ForecastData> forecastData = new MutableLiveData<>();
    private MutableLiveData<String> currentCity = new MutableLiveData<>();
    private WeatherDataManager weatherDataManager;

    public WeatherViewModel(Application application) {
        super(application);
        weatherDataManager = new WeatherDataManager(getApplication().getApplicationContext(), this);
    }

    public void setWeatherData(WeatherData weather) {
        weatherData.setValue(weather);
    }

    public void setForecastData(ForecastData forecast) {
        forecastData.setValue(forecast);
    }
    public LiveData<WeatherData> getWeatherData() {
        return weatherData;
    }

    public LiveData<ForecastData> getForecastData() {
        return forecastData;
    }

    public void setCurrentCity(String city, boolean fetch) {
        if (!city.equals(currentCity.getValue())) {
            currentCity.setValue(city);
            Log.i("WeatherVM_setCurrentCity", "City changed to " + currentCity.getValue());
            if (fetch) {
                fetchData();
            }
        }
    }

    public void fetchData() {
        Log.i("WeatherVM_FetchData", "Fetching data for: " + currentCity.getValue());
        weatherDataManager.fetchData(currentCity.getValue());
//        setForecastData(weatherDataManager.getForecastData(currentCity.getValue(), true));
//        setWeatherData(weatherDataManager.getWeatherData(currentCity.getValue(), true));
    }

    public void saveCity(String city) {
        weatherDataManager.addToSavedCities(city);
    }

    public void removeSavedCity(String city) {
        weatherDataManager.removeFromSavedCities(city);
    }

    public boolean isCitySaved(String city) {
        return weatherDataManager.isCitySaved(city);
    }

    public HashSet<String> getSavedCities() {
        return weatherDataManager.getSavedCities();
    }


    public LiveData<String> getCurrentCity() {
        return currentCity;
    }
}