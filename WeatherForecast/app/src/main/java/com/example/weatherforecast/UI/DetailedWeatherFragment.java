package com.example.weatherforecast.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.R;
import com.example.weatherforecast.utils.TemperatureUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DetailedWeatherFragment extends Fragment {

    private WeatherViewModel weatherVM;

    public DetailedWeatherFragment(WeatherViewModel vm) {
        super();
        this.weatherVM = vm;
    }

    public DetailedWeatherFragment() { super(); }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("vm", weatherVM);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(weatherVM == null && savedInstanceState != null) {
            weatherVM = savedInstanceState.getSerializable("vm", WeatherViewModel.class);
        }
        View view = inflater.inflate(R.layout.fragment_detailed_weather, container, false);

        final Observer<WeatherData> weatherDataObserver = this::updateUI;

        weatherVM.getWeatherData().observe(getViewLifecycleOwner(), weatherDataObserver);

        if(view.findViewById(R.id.city) != null) {
            view.findViewById(R.id.city).setOnClickListener(v -> showCityInputDialog());
        }

        return view;
    }

    private void showCityInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter City Name");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String city = input.getText().toString();
            if (!city.isEmpty()) {
                weatherVM.setCurrentCity(city, false);
                Log.i("CityInputDialog", "Changing city to: " + city);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateUI(WeatherData weather) {
        Log.i("updateUI", "Updating UI for weather.name = " + weather);
        if (weather != null) {
            TextView tempValue = getView().findViewById(R.id.detailed_temperature);

            String formattedTemp = TemperatureUtil.convertTemperature(getContext(), weather.main.temp);

            String[] tempParts = formattedTemp.split(" ");
            if (tempParts.length == 2) {
                String tempValueStr = tempParts[0];
                String tempUnitStr = tempParts[1];

                tempValue.setText(tempValueStr);
            } else {
                tempValue.setText(formattedTemp);
            }

            TextView pressure = getView().findViewById(R.id.detailed_pressure);
            TextView humidity = getView().findViewById(R.id.detailed_humidity);
            TextView windSpeed = getView().findViewById(R.id.detailed_windSpeed);
            TextView windDirection = getView().findViewById(R.id.detailed_windDirection);
            TextView visibility = getView().findViewById(R.id.detailed_visibility);
            TextView time = getView().findViewById(R.id.detailed_time);
            TextView city = getView().findViewById(R.id.city);

            if(city != null) {
                city.setText(weather.name);
            }


            String humidityText = weather.main.humidity + "%";
            humidity.setText(humidityText);

            String windSpeedTxt = weather.wind.speed + " m/s";
            windSpeed.setText(windSpeedTxt);

            String windDirectionTxt = weather.wind.deg + " °";
            windDirection.setText(windDirectionTxt);

            String visibilityTxt = weather.visibility + " m";
            visibility.setText(visibilityTxt);

            String pressureText = weather.main.pressure + " hPa";
            pressure.setText(pressureText);

            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            timeZone.setRawOffset(weather.timezone * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(timeZone);
            time.setText(dateFormat.format(new Date(weather.dt * 1000L)));

        } else {
            Log.e("UpdateUI", "Weather was null.");
        }
    }
}