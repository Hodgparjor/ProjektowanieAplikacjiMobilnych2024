package com.example.weatherforecast.UI;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.weatherforecast.DataAcquisition.WeatherDataManager;
import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;

public class WeatherViewModel extends AndroidViewModel {
    private MutableLiveData<WeatherData> weatherData = new MutableLiveData<>();
    private MutableLiveData<ForecastData> forecastData = new MutableLiveData<>();
    private MutableLiveData<String> currentCity = new MutableLiveData<>();
    private WeatherDataManager weatherDataManager;

    public WeatherViewModel(Application application) {
        super(application);
        weatherDataManager = new WeatherDataManager(getApplication().getApplicationContext());
    }

    public void setWeatherData(WeatherData weather) {
        weatherData.postValue(weather);
    }

    public void setForecastData(ForecastData forecast) {
        forecastData.postValue(forecast);
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
            currentCity.postValue(city);
            Log.i("WeatherVM_setCurrentCity", "City changed to " + currentCity.getValue());
            if (fetch) {
                fetchData();
            }
        }
    }

    public void fetchData() {
        Log.i("WeatherVM_FetchData", "Fetching data for: " + currentCity.getValue());
        weatherDataManager.fetchData(currentCity.getValue());
        setForecastData(weatherDataManager.getForecastData(currentCity.getValue(), false));
        setWeatherData(weatherDataManager.getWeatherData(currentCity.getValue(), false));
    }


    public LiveData<String> getCurrentCity() {
        return currentCity;
    }
}