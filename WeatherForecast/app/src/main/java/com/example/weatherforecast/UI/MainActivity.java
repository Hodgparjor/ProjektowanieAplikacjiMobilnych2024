package com.example.weatherforecast.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherforecast.R;
import com.example.weatherforecast.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String FRAGMENT_WEATHER = "fragment_weather";
    private static final String FRAGMENT_FORECAST = "fragment_forecast";
    private static final String FRAGMENT_DETAILED = "fragment_detailed_weather";
    private String activeFragment = FRAGMENT_WEATHER;
    private WeatherViewModel weatherViewModel;
    private String currentCity = "Łódź";

    private static boolean isMobile = true;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable fetchDataRunnable = new Runnable() {
        @Override
        public void run() {
            String currentCity = weatherViewModel.getCurrentCity().getValue();
            if (currentCity != null && !currentCity.isEmpty()) {
                fetchInitialData(currentCity);
            }

            handler.postDelayed(this, 600000); // Refresh data each 10 minutes
        }
    };

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
        InitializeActivity();
        loadPreferencesAndInitialData(savedInstanceState);
        handler.post(fetchDataRunnable);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("current_fragment_tag", activeFragment);
        outState.putString("current_city", currentCity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(fetchDataRunnable);
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(fetchDataRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(fetchDataRunnable);
    }

    private void initializeFragments() {
        checkLayout();
        if(isMobile) {
            initializeMobileFragments();
        } else {
            initializeTabletFragments();
        }
    }

    private void checkLayout() {
        isMobile = findViewById(R.id.fragmentFrame1) == null;
    }
    private void initializeTabletFragments() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentFrame1) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentFrame1, new WeatherFragment(weatherViewModel))
                    .commit();
        }
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentFrame2) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentFrame2, new DetailedWeatherFragment(weatherViewModel))
                    .commit();
        }
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentFrame3) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentFrame3, new ForecastFragment(weatherViewModel))
                    .commit();
        }
    }


    private void initializeMobileFragments() {
        loadFragment(FRAGMENT_WEATHER);
        findViewById(R.id.btnWeather).setOnClickListener(v -> loadFragment(FRAGMENT_WEATHER));
        findViewById(R.id.btnDetails).setOnClickListener(v -> loadFragment(FRAGMENT_DETAILED));
        findViewById(R.id.btnForecast).setOnClickListener(v -> loadFragment(FRAGMENT_FORECAST));
    }

    private void InitializeActivity() {
        initializeFragments();
        findViewById(R.id.btnSettings).setOnClickListener(v -> showSettings());
        ((CheckBox)findViewById(R.id.saveCheckBox)).setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()) {
                weatherViewModel.saveCity(currentCity);
            } else {
                weatherViewModel.removeSavedCity(currentCity);
            }
        });
        initializeObservers();
    }

    private void initializeObservers() {
        weatherViewModel.getCurrentCity().observe(this, city -> {
            if(!city.equals(currentCity)) {
                Log.i("WeatherVM_CityObserver", "Changing MainActivity current city from " + currentCity + " to " + city);
                currentCity = city;
                fetchInitialData(currentCity);
                ((CheckBox)findViewById(R.id.saveCheckBox)).setChecked(weatherViewModel.isCitySaved(city));
            }
        });
    }

    private void loadFragment(String fragmentTag) {
        AppCompatButton weatherButton = findViewById(R.id.btnWeather);
        AppCompatButton detailsButton = findViewById(R.id.btnDetails);
        AppCompatButton forecastButton = findViewById(R.id.btnForecast);
        Fragment fragment;
        activeFragment = fragmentTag;
        @ColorInt
        int secondaryRedColor = ContextCompat.getColor(getApplicationContext(), R.color.secondary_red);
        int baseTextColor = ContextCompat.getColor(getApplicationContext(), R.color.primary);

        switch (fragmentTag) {
            case FRAGMENT_WEATHER:
                fragment = new WeatherFragment(weatherViewModel);
                weatherButton.setTextColor(secondaryRedColor);
                detailsButton.setTextColor(baseTextColor);
                forecastButton.setTextColor(baseTextColor);
                break;
            case FRAGMENT_DETAILED:
                fragment = new DetailedWeatherFragment(weatherViewModel);
                weatherButton.setTextColor(baseTextColor);
                detailsButton.setTextColor(secondaryRedColor);
                forecastButton.setTextColor(baseTextColor);
                break;
            case FRAGMENT_FORECAST:
                fragment = new ForecastFragment(weatherViewModel);
                weatherButton.setTextColor(baseTextColor);
                detailsButton.setTextColor(baseTextColor);
                forecastButton.setTextColor(secondaryRedColor);
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
        } else if (!city.equals(weatherViewModel.getCurrentCity().getValue())) {
            weatherViewModel.setCurrentCity(city, false);
        }
        weatherViewModel.fetchData();
        if (NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "fetching data for " + city, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Internet connection unavaliable, loading last saved data for " + city + ". WARNING DATA MIGHT BE OUTDATED!.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPreferencesAndInitialData(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("WeatherlyPreferences", MODE_PRIVATE);
        currentCity = prefs.getString("defaultCity", "Łódź");
        Log.i("loadPreferencesAndInitialData", "City loaded from preferences: " + currentCity);
        if (savedInstanceState != null) {
            activeFragment = savedInstanceState.getString("current_fragment_tag", FRAGMENT_WEATHER);
            currentCity = savedInstanceState.getString("current_city", "Łódź");
        } else {
            fetchInitialData(currentCity);
        }
    }

    private void showSettings() {
        LayoutInflater inflater = getLayoutInflater();
        View settingsView = inflater.inflate(R.layout.settings, null);
        SharedPreferences preferences = getSharedPreferences("WeatherlyPreferences", MODE_PRIVATE);
        String defaultCity = preferences.getString("defaultCity", "Łódź");


        //RecyclerView savedCities = settingsView.findViewById(R.id.recyclerViewSavedCities);
        RadioGroup temperatureRadio = settingsView.findViewById(R.id.temperatureUnitRadioGroup);
        AutoCompleteTextView defaultCityTextView = settingsView.findViewById(R.id.defaultCityAutoCompleteTextView);

        ListView savedCities = settingsView.findViewById(R.id.listViewSavedCities);
        List<String> savedCitiesList = new ArrayList<String>(weatherViewModel.getSavedCities());

        savedCities.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, savedCitiesList) {

        });



        String temperatureUnit = preferences.getString("temperatureUnit", "K");
        switch (temperatureUnit) {
            case "K":
                temperatureRadio.check(R.id.radioKelvin);
                break;
            case "C":
                temperatureRadio.check(R.id.radioCelsius);
                break;
            case "F":
                temperatureRadio.check(R.id.radioFahrenheit);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(settingsView)
                .setTitle("Settings")
                .setPositiveButton("OK", (dialogInterface, which) -> {
                    String selectedCity = defaultCityTextView.getText().toString();
                    preferences.edit().putString("defaultCity", selectedCity).apply();
                    updateTemperatureUnit(temperatureRadio);
                    fetchInitialData(selectedCity);
                })
                .setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        savedCities.setOnItemClickListener((parent, view, position, id) -> {
            String clickedCity = savedCitiesList.get(position);
            if(!currentCity.equals(clickedCity)) {
                weatherViewModel.setCurrentCity(clickedCity, false);
            }
            dialog.dismiss();
        });
        settingsView.findViewById(R.id.btnRefreshSettings).setOnClickListener(v -> {
            if (!currentCity.isEmpty()) {
                fetchInitialData(currentCity);
            } else {
                Log.e("MainActivity", "Current city is empty");
            }
            dialog.dismiss();
        });
        defaultCityTextView.setText(defaultCity);
        dialog.show();
    }

    private void updateTemperatureUnit(RadioGroup radioGroup) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        String unit = "K";
        if (checkedId == R.id.radioCelsius) {
            unit = "C";
        } else if (checkedId == R.id.radioFahrenheit) {
            unit = "F";
        }
        SharedPreferences preferences = getSharedPreferences("WeatherlyPreferences", MODE_PRIVATE);
        preferences.edit().putString("temperatureUnit", unit).apply();
    }
}