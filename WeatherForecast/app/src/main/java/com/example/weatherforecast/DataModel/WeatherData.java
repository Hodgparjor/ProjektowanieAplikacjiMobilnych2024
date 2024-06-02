package com.example.weatherforecast.DataModel;

import java.util.List;

public class WeatherData {

    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public int visibility;
    public Wind wind;
    public Rain rain;
    public Clouds clouds;
    public long dt;
    public Sys sys;
    public int timezone;
    public int id;
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

    public static class Rain {
        public double h1;
        public double h3;
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
