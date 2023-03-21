package com.github.campus_capture.bootcamp.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.campus_capture.bootcamp.storage.entities.Zone;

import java.util.List;

@Dao
public interface ZoneDAO {
    @Query("SELECT * FROM zone")
    List<Zone> getAll();

    @Query("SELECT * FROM zone WHERE uid IN (:zoneIds)")
    List<Zone> loadAllByIds(int[] zoneIds);

    @Query("SELECT * FROM zone WHERE name LIKE :name LIMIT 1")
    Zone findByName(String name);

    @Insert
    void insertAll(Zone... zones);

    @Delete
    void delete(Zone zone);
}
