package com.github.campus_capture.bootcamp.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.github.campus_capture.bootcamp.storage.converters.LatLngListConverter;
import com.github.campus_capture.bootcamp.storage.dao.ZoneDAO;
import com.github.campus_capture.bootcamp.storage.entities.Zone;

@Database(entities = {Zone.class}, version = 1)
@TypeConverters({LatLngListConverter.class})
public abstract class ZoneDatabase extends RoomDatabase {
    public abstract ZoneDAO zoneDAO();
}
