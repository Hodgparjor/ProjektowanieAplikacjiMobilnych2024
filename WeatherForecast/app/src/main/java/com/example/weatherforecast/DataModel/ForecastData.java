package com.example.weatherforecast.DataModel;

import java.io.Serializable;
import java.util.List;

public class ForecastData implements Serializable {
    public String cod;
    public int message;
    public int cnt;
    public List<Forecast> list;

    public static class Forecast {
        public long dt;
        public Main main;
        public List<Weather> weather;
        public Clouds clouds;
        public Wind wind;
        public String dt_txt;
    }

    public static class Main {
        public double temp;
        public double feels_like;
        public int pressure;
        public int humidity;
    }

    public static class Weather {
        public String description;
        public String icon;
    }

    public static class Clouds {
        public int all;
    }

    public static class Wind {
        public double speed;
        public int deg;
    }
}
