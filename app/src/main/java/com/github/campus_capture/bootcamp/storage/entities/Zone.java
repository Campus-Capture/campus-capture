package com.github.campus_capture.bootcamp.storage.entities;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/***
 * Class defining a Zone on a map.
 * It is an Entity that can be stored in a database
 */
@Entity
public class Zone {
    @PrimaryKey
    private int uid;

    private String name;

    // TODO add the current owner, of type Section

    private List<LatLng> vertices;

    public Zone(String name, List<LatLng> vertices){
        this.name = name;
        this.vertices = vertices;
    }

    @Override
    public boolean equals(@Nullable Object otherZone) {
        if (!name.equals(((Zone) otherZone).getName())) {
            return false;
        }
        for(int i = 0; i < vertices.size(); ++i){
            if (!vertices.get(i).equals(((Zone) otherZone).getVertices().get(i))){
                return false;
            }
        }

        return true;
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

    public void setVertices(List<LatLng> vertices) {
        this.vertices = vertices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
