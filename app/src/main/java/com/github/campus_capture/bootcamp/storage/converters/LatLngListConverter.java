package com.github.campus_capture.bootcamp.storage.converters;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Object that can serialize a List of LatLng objects to text and back.
 * It is used as a TypeConverter to store in a Room Persistent Storage Database
 * 
 * The LatLng objects are store as string in the following format : <lat>|<lon>#
 */
public class LatLngListConverter {
    @TypeConverter
    public List<LatLng> storedStringToLatLng(String value) {
        List<LatLng> vertices = new ArrayList<>();

        String[] coordsPairs = value.split(" ");

        for (String coords : coordsPairs){
            String lat = coords.split(",")[0];
            String lon = coords.split(",")[1];
            vertices.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
        }

        return vertices;
    }

    @TypeConverter
    public String latLngToStoredString(List<LatLng> vertices) {
        StringBuilder value = new StringBuilder();

        for (LatLng vertex : vertices)
            value.append(vertex.latitude).append(",").append(vertex.longitude).append(" ");

        return value.toString();
    }
}
