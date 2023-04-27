package com.github.campus_capture.bootcamp.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.github.campus_capture.bootcamp.storage.entities.Zone;

import java.util.List;

/***
 * Data Access Object that carries the queries possible on a Zone object representation in
 * database
 */
@Dao
public interface ZoneDAO {
    @Query("SELECT * FROM zone")
    List<Zone> getAll();

    @Query("SELECT * FROM zone WHERE uid IN (:zoneIds)")
    List<Zone> loadAllByIds(int[] zoneIds);

    @Query("SELECT * FROM zone WHERE name LIKE :name LIMIT 1")
    Zone findByName(String name);

    @Insert
    List<Long> insertAll(Zone... zones);

    @Insert
    long insert(Zone zone);

    @Delete
    int delete(Zone zone);
}
