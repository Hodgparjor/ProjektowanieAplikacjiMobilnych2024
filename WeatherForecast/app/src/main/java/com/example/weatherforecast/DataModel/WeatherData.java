package com.example.weatherforecast.DataModel;

import java.io.Serializable;
import java.util.List;

public class WeatherData implements Serializable {
    public Coord coord;
    public List<Weather> weather;
    public Main main;
    public String base;
    public int visibility;
    public Wind wind;
    public Clouds clouds;
    public long dt;
    public Sys sys;
    public int timezone;
    public long id;
    public String name;
    public int cod;

    public static class Coord {
        public double lon;
        public double lat;
    }

    public static class Weather {
        public int id;
        public String main;
        public String description;
        public String icon;
    }

    public static class Main {
        public double temp;
        public double feels_like;
        public double temp_min;
        public double temp_max;
        public int pressure;
        public int humidity;
    }

    public static class Wind {
        public double speed;
        public int deg;
    }

    public static class Clouds {
        public int all;
    }

    public static class Sys {
        public int type;
        public long id;
        public String country;
        public long sunrise;
        public long sunset;
    }
}
