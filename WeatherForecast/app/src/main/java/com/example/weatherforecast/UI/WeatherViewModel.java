package com.example.weatherforecast.UI;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherforecast.DataAcquisition.WeatherDataManager;
import com.example.weatherforecast.DataAcquisition.WeatherService;
import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;

public class WeatherViewModel extends AndroidViewModel {
    // TODO: Implement the ViewModel
    private final MutableLiveData<WeatherData> weatherData = new MutableLiveData<>();
    private MutableLiveData<ForecastData> forecastData = new MutableLiveData<>();
    private MutableLiveData<String> currentCity = new MutableLiveData<>();
    private WeatherDataManager weatherDataManager;

    public WeatherViewModel() {
        super();
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
            if (fetch) {
                weatherDataManager.getData(city);
            }
        }
    }

    public LiveData<String> getCurrentCity() {
        return currentCity;
    }
}