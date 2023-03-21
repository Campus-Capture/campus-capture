package com.github.campus_capture.bootcamp.storage.converters;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class VertexConverter {
    @TypeConverter
    public List<LatLng> storedStringToLatLng(String value) {
        List<LatLng> vertices = new ArrayList<>();

        String[] coordsPairs = value.split("#");

        for (String coords : coordsPairs){
            String lat = coords.split("\\|")[0];
            String lon = coords.split("\\|")[1];
            vertices.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)));
        }

        return vertices;
    }

    @TypeConverter
    public String latLngToStoredString(List<LatLng> vertices) {
        String value = "";

        for (LatLng vertex : vertices)
            value += vertex.latitude +  "|" + vertex.longitude + "#";

        return value;
    }
}
