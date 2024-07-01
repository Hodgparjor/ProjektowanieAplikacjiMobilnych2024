package com.example.weatherforecast.UI;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.weatherforecast.DataModel.WeatherData;
import com.example.weatherforecast.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherFragment extends Fragment {

    private WeatherViewModel weatherVM;

    public WeatherFragment() {}

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherVM = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);

//        weatherVM.getWeatherData().observe(getViewLifecycleOwner(), this::updateUI);
//
//        cityTextView = view.findViewById(R.id.city);
//        cityTextView.setOnClickListener(v -> showCityInputDialog());

        return view;
        //return inflater.inflate(R.layout.fragment_weather, container, false);
    }

//    private void updateUI(WeatherData weather) {
//        if (weather != null) {
////            TextView tempLabel = getView().findViewById(R.id.);
//            TextView tempValue = getView().findViewById(R.id.temperature);
//
////            String formattedTemp = TemperatureConverter.convertToPreferredUnit(getContext(), weather.main.temp);
//
//            String[] tempParts = formattedTemp.split(" ");
//            if (tempParts.length == 2) {
//                String tempValueStr = tempParts[0];
//                String tempUnitStr = tempParts[1];
//
//                tempLabel.setText("Temp (" + tempUnitStr + ")");
//                tempValue.setText(tempValueStr);
//            } else {
//                tempLabel.setText("Temp");
//                tempValue.setText(formattedTemp);
//            }
//
//            TextView lat = getView().findViewById(R.id.lat);
//            TextView lon = getView().findViewById(R.id.lon);
//            TextView description = getView().findViewById(R.id.description);
//            TextView pressure = getView().findViewById(R.id.pressure);
//            TextView time = getView().findViewById(R.id.time);
//
//            cityTextView.setText(weather.name);
//
//            lat.setText(weather.coord.lat + ", ");
//            lon.setText(weather.coord.lon + "");
//
//            description.setText(weather.weather.get(0).description);
//
//            pressure.setText(weather.main.pressure + "");
//
//            TimeZone timeZone = TimeZone.getTimeZone("GMT");
//            timeZone.setRawOffset(weather.timezone * 1000);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            dateFormat.setTimeZone(timeZone);
//            time.setText(dateFormat.format(new Date(weather.dt * 1000L)));
//
//            updateWeatherIcon(weather.weather.get(0).description, R.id.weatherWidget);
//        }
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        weatherVM = new ViewModelProvider(this).get(WeatherViewModel.class);
        // TODO: Use the ViewModel
    }

}