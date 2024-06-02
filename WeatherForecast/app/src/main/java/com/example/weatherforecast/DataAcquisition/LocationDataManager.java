package com.example.weatherforecast.DataAcquisition;

import com.example.weatherforecast.DataModel.Coords;

import java.util.HashMap;

public class LocationDataManager {

    public static HashMap<String, Coords> locations = new HashMap<String, Coords>();

    static public Coords getCoords(String location) {
        if(locations.containsKey(location)) {
            return locations.get(location);
        } else {
            Coords newCords;
            //TODO implement getting coords based on string location


            //locations.put(location, newCords);
            return new Coords(44.34, 10.99);
        }
    }
}

