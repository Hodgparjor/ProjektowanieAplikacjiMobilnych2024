package com.example.weatherforecast.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.R;
import com.example.weatherforecast.utils.TemperatureUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherFragment extends Fragment {

    private WeatherViewModel weatherVM;

    public WeatherFragment(WeatherViewModel vm) {
        this.weatherVM = vm;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        final Observer<WeatherData> weatherDataObserver = this::updateUI;

        weatherVM.getWeatherData().observe(getViewLifecycleOwner(), weatherDataObserver);
        weatherVM.fetchData();

        view.findViewById(R.id.city).setOnClickListener(v -> showCityInputDialog());

        return view;
    }


    private void updateUI(WeatherData weather) {
        Log.i("updateUI", "Updating UI for weather.name = " + weather);
        if (weather != null) {
          TextView tempValue = getView().findViewById(R.id.temperature);

            String formattedTemp = TemperatureUtil.convertTemperature(getContext(), weather.main.temp);

            String[] tempParts = formattedTemp.split(" ");
            if (tempParts.length == 2) {
                String tempValueStr = tempParts[0];
                String tempUnitStr = tempParts[1];

                tempValue.setText("Temp (" + tempUnitStr + ")");
                tempValue.setText(tempValueStr);
            } else {
                tempValue.setText("Temp");
                tempValue.setText(formattedTemp);
            }

            TextView coords = getView().findViewById(R.id.coords);
            TextView description = getView().findViewById(R.id.description);
            TextView pressure = getView().findViewById(R.id.pressureValue);
            TextView time = getView().findViewById(R.id.timeValue);
            TextView city = getView().findViewById(R.id.city);

            city.setText(weather.name);

            String coordsText = weather.coord.lat + ", " + weather.coord.lon;
            coords.setText(coordsText);

            description.setText(weather.weather.get(0).description);

            String pressureText = weather.main.pressure + " hPa";
            pressure.setText(pressureText);

            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            timeZone.setRawOffset(weather.timezone * 1000);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(timeZone);
            time.setText(dateFormat.format(new Date(weather.dt * 1000L)));

            updateWeatherIcon(weather.weather.get(0).description, R.id.weatherIcon);
        } else {
            Log.e("UpdateUI", "Weather was null.");
        }
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

    private void updateWeatherIcon(String description, int imageViewId) {
        int drawableRes = R.drawable.clearsky;
        description = description.toLowerCase(Locale.ROOT);

        if (description.contains("clear sky")) {
            drawableRes = R.drawable.clearsky;
        } else if (description.contains("few clouds")) {
            drawableRes = R.drawable.fewclouds;
        } else if (description.contains("scattered clouds")) {
            drawableRes = R.drawable.scatteredclouds;
        } else if (description.contains("broken clouds") || description.contains("overcast clouds")) {
            drawableRes = R.drawable.brokenclouds;
        } else if (description.contains("shower rain")) {
            drawableRes = R.drawable.showerrain;
        } else if (description.contains("rain")) {
            drawableRes = R.drawable.rain;
        } else if (description.contains("thunderstorm")) {
            drawableRes = R.drawable.thunderstorm;
        } else if (description.contains("snow")) {
            drawableRes = R.drawable.snow;
        } else if (description.contains("mist")) {
            drawableRes = R.drawable.mist;
        }

        ImageView imageView = getView().findViewById(imageViewId);
        imageView.setImageResource(drawableRes);
    }


}