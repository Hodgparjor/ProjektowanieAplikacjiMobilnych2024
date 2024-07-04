package com.example.weatherforecast.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherforecast.DataModel.ForecastData;
import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.R;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class ForecastFragment extends Fragment {

    private WeatherViewModel weatherVM;

    public ForecastFragment(WeatherViewModel vm) {
        this.weatherVM = vm;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        final Observer<ForecastData> forecastDataObserver = this::updateUI;

        weatherVM.getForecastData().observe(getViewLifecycleOwner(), forecastDataObserver);

        //view.findViewById(R.id.city).setOnClickListener(v -> showCityInputDialog());

        return view;
    }

    private void updateUI(ForecastData forecast) {
        if(forecast == null) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        TextView city = getView().findViewById(R.id.city);

    }

}