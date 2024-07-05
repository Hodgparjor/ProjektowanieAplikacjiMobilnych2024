package com.example.weatherforecast.UI;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.R;
import com.example.weatherforecast.utils.TemperatureUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ForecastFragment extends Fragment {

    private WeatherViewModel weatherVM;

    public ForecastFragment(WeatherViewModel vm) {
        super();
        this.weatherVM = vm;
    }

    public ForecastFragment() { super(); };

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
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        final Observer<ForecastData> forecastDataObserver = this::updateUI;

        weatherVM.getForecastData().observe(getViewLifecycleOwner(), forecastDataObserver);

        if(view.findViewById(R.id.city) != null) {
            view.findViewById(R.id.city).setOnClickListener(v -> showCityInputDialog());
        }

        return view;
    }

    private void updateUI(ForecastData forecast) {
        if(forecast == null) {
            return;
        }
        TextView city = getView().findViewById(R.id.city);
        if(city != null) {
            city.setText(weatherVM.getWeatherData().getValue().name);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (int i = 0; i < Math.min(4, forecast.list.size()); i++) {
            ForecastData.Forecast f = forecast.list.get(i);
            int hour = i * 3;
            String timeText = sdf.format(new Date(f.dt * 1000));
            String formattedTemp = TemperatureUtil.convertTemperature(getContext(), f.main.temp);
            String tempText = formattedTemp;
            TextView timeTextView;
            TextView tempTextView;
            ImageView weatherImageView;

            if(hour == 0) {
                timeTextView = getView().findViewById(R.id.time_3h);
                tempTextView = getView().findViewById(R.id.temp_3h);
                weatherImageView = getView().findViewById(R.id.icon_3h);
            } else  if (hour == 3) {
                timeTextView = getView().findViewById(R.id.time_6h);
                tempTextView = getView().findViewById(R.id.temp_6h);
                weatherImageView = getView().findViewById(R.id.icon_6h);
            } else if (hour == 6) {
                timeTextView = getView().findViewById(R.id.time_9h);
                tempTextView = getView().findViewById(R.id.temp_9h);
                weatherImageView = getView().findViewById(R.id.icon_9h);
            } else {
                timeTextView = getView().findViewById(R.id.time_12h);
                tempTextView = getView().findViewById(R.id.temp_12h);
                weatherImageView = getView().findViewById(R.id.icon_12h);
            }

            timeTextView.setText(timeText);
            tempTextView.setText(tempText);
            weatherImageView.setImageResource(getWeatherIcon(f.weather.get(0).description));
        }

        sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        for (int i = 0; i < 5; i++) {
            if (forecast.list.size() > i * 8 + 5) {
                ForecastData.Forecast daily = forecast.list.get(i * 8 + 5);
                TextView tempTextView;
                TextView dayTextView;
                ImageView weatherImageView;
                String formattedTemp = TemperatureUtil.convertTemperature(getContext(), daily.main.temp);

                switch(i) {
                    case 0:
                        tempTextView = getView().findViewById(R.id.dayTemp_today);
                        dayTextView = getView().findViewById(R.id.dayText_today);
                        weatherImageView = getView().findViewById(R.id.dayImg_today);
                        break;
                    case 1:
                        tempTextView = getView().findViewById(R.id.dayTemp_1);
                        dayTextView = getView().findViewById(R.id.dayText_1);
                        weatherImageView = getView().findViewById(R.id.dayImg_1);
                        break;
                    case 2:
                        tempTextView = getView().findViewById(R.id.dayTemp_2);
                        dayTextView = getView().findViewById(R.id.dayText_2);
                        weatherImageView = getView().findViewById(R.id.dayImg_2);
                        break;
                    case 3:
                        tempTextView = getView().findViewById(R.id.dayTemp_3);
                        dayTextView = getView().findViewById(R.id.dayText_3);
                        weatherImageView = getView().findViewById(R.id.dayImg_3);
                        break;
                    default:
                        tempTextView = getView().findViewById(R.id.dayTemp_4);
                        dayTextView = getView().findViewById(R.id.dayText_4);
                        weatherImageView = getView().findViewById(R.id.dayImg_4);
                        break;
                }
                tempTextView.setText(formattedTemp);
                weatherImageView.setImageResource(getWeatherIcon(daily.weather.get(0).description));
                dayTextView.setText(sdf.format(new Date(daily.dt * 1000)));
            }
        }

    }

    private int getWeatherIcon(String description) {
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
        return drawableRes;
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

}