package com.example.weatherforecast.UI;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherforecast.DataAcquisition.WeatherDataManager;
import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.R;
import com.example.weatherforecast.utils.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_WEATHER = "fragment_weather";
    private static final String FRAGMENT_FORECAST = "fragment_forecast";
    private static final String FRAGMENT_DETAILED = "fragment_detailed_weather";
    private String activeFragment = FRAGMENT_WEATHER;
    private WeatherViewModel weatherViewModel;
    private Handler handler = new Handler(Looper.getMainLooper());

//    private Runnable fetchDataRunnable = new Runnable() {
//        @Override
//        public void run() {
//            String currentCity = weatherViewModel.getCurrentCitySafely();
//            if (currentCity != null && !currentCity.isEmpty()) {
//                fetchInitialData(currentCity);
//            }
//
//            handler.postDelayed(this, 900000);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        initializeMobileFragments();
    }

    private void initializeMobileFragments() {
        findViewById(R.id.btnWeather).setOnClickListener(v -> loadFragment(FRAGMENT_WEATHER));
        findViewById(R.id.btnDetails).setOnClickListener(v -> loadFragment(FRAGMENT_DETAILED));
        findViewById(R.id.btnForecast).setOnClickListener(v -> loadFragment(FRAGMENT_FORECAST));
    }

    private void loadFragment(String fragmentTag) {
        Fragment fragment;
        activeFragment = fragmentTag;

        switch (fragmentTag) {
            case FRAGMENT_WEATHER:
                fragment = new WeatherFragment();
                break;
            case FRAGMENT_DETAILED:
                fragment = new DetailedWeatherFragment();
                break;
            case FRAGMENT_FORECAST:
                fragment = new ForecastFragment();
                break;
            default:
                throw new IllegalArgumentException("Unknown fragment tag: " + fragmentTag);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentFrame, fragment)
                .commit();
    }

    private void fetchInitialData(String city) {
        if (city == null) {
            Log.e("fetchInitialData", "City is null");
            return;
        }
        if (NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "fetching data for " + city, Toast.LENGTH_SHORT).show();
            weatherViewModel.getWeatherData();
            weatherViewModel.getForecastData();
        }
//        else {
//            WeatherData weather = weatherViewModel.get;
//            ForecastData forecast = loadForecastDataFromFile();
//            if (weather != null && forecast != null) {
//                weatherViewModel.setWeatherData(weather);
//                weatherViewModel.setForecastData(forecast);
//            }
//            showNoInternetDialog();
//        }
//        weatherViewModel.setCurrentCity(city, false);
//        updateSaveButtonIcon(city);
    }
}