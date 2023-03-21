package com.github.campus_capture.bootcamp.storage.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

@Entity
public class Zone {
    @PrimaryKey
    public int uid;
    public String name;

    public List<LatLng> vertices;

    public Zone(String name, List<LatLng> vertices){
        this.name = name;
        this.vertices = vertices;
    }

    @NonNull
    @Override
    public String toString() {
        String text = "Zone named " + name + " with the following vertices:\n";
        for(LatLng vertex : vertices){
            text += "Lat: " + vertex.latitude + "\n";
            text += "Lon: " + vertex.longitude + "\n";
        }

        return text;
    }

    public List<LatLng> getVertices() {
        return vertices;
    }
}
