package com.github.campus_capture.bootcamp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.storage.ZoneDatabase;
import com.github.campus_capture.bootcamp.storage.dao.ZoneDAO;
import com.github.campus_capture.bootcamp.storage.entities.Zone;
import com.google.android.gms.maps.model.LatLng;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@RunWith(AndroidJUnit4.class)
public class ZoneDAOTest {
    private ZoneDAO zoneDAO;
    private ZoneDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ZoneDatabase.class).build();
        zoneDAO = db.zoneDAO();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeZoneAndReadInList() throws Exception {
        List<LatLng> vertices1 = new ArrayList<>();
        vertices1.add(new LatLng(10, 20));
        vertices1.add(new LatLng(0, 0));
        vertices1.add(new LatLng(-1000, 20.1));
        Zone zone1 = new Zone("zone1", vertices1);

        zoneDAO.insertAll(zone1);
        Zone byName1 = zoneDAO.findByName("zone1");

        assertThat(byName1.equals(zone1), equalTo(true));
    }

    @Test
    public void writeZoneAndGetAll() throws Exception {
        List<LatLng> vertices1 = new ArrayList<>();
        vertices1.add(new LatLng(10, 20));
        vertices1.add(new LatLng(0, 0));
        vertices1.add(new LatLng(-1000, 20.1));

        List<LatLng> vertices2 = new ArrayList<>();
        vertices2.add(new LatLng(10, 2));
        vertices2.add(new LatLng(9, 0));
        vertices2.add(new LatLng(-100, -10));

        Zone zone1 = new Zone("zone1", vertices1);
        Zone zone2 = new Zone("zone2", vertices2);

        List<Zone> zoneArray = new ArrayList();
        zoneArray.add(zone1);
        zoneArray.add(zone2);

        zoneDAO.insertAll(zone1, zone2);

        List<Zone> zoneArrayDB = zoneDAO.getAll();

        for(int i = 0; i < zoneArray.size(); ++i){
            assertThat(zoneArray.get(i).equals(zoneArrayDB.get(i)), equalTo(true));
        }
    }

    @Test
    public void writeZoneAndloadAll() throws Exception {
        List<LatLng> vertices1 = new ArrayList<>();
        vertices1.add(new LatLng(10, 20));
        vertices1.add(new LatLng(0, 0));
        vertices1.add(new LatLng(-1000, 20.1));

        List<LatLng> vertices2 = new ArrayList<>();
        vertices2.add(new LatLng(10, 2));
        vertices2.add(new LatLng(9, 0));
        vertices2.add(new LatLng(-100, -10));

        Zone zone1 = new Zone("zone1", vertices1);
        Zone zone2 = new Zone("zone2", vertices2);

        List<Zone> zoneArray = new ArrayList();
        zoneArray.add(zone1);
        zoneArray.add(zone2);

        zoneDAO.insertAll(zone1, zone2);

        int [] zoneList = IntStream.rangeClosed(0, zoneArray.size()).toArray();

        List<Zone> zoneArrayDB = zoneDAO.loadAllByIds(zoneList);

        for(int i = 0; i < zoneArray.size(); ++i){
            assertThat(zoneArray.get(i).equals(zoneArrayDB.get(i)), equalTo(true));
        }
    }

    @Test
    public void writeZoneAndDelete() throws Exception {
        List<LatLng> vertices1 = new ArrayList<>();
        vertices1.add(new LatLng(10, 20));
        vertices1.add(new LatLng(0, 0));
        vertices1.add(new LatLng(-1000, 20.1));

        List<LatLng> vertices2 = new ArrayList<>();
        vertices2.add(new LatLng(10, 2));
        vertices2.add(new LatLng(9, 0));
        vertices2.add(new LatLng(-100, -10));

        Zone zone1 = new Zone("zone1", vertices1);
        Zone zone2 = new Zone("zone2", vertices2);

        List<Zone> zoneArray = new ArrayList();
        zoneArray.add(zone1);
        zoneArray.add(zone2);

        zoneDAO.insertAll(zone1, zone2);

        List<Zone> zoneArrayDB = zoneDAO.getAll();

        for(int i = 0; i < zoneArray.size(); ++i){
            assertThat(zoneArray.get(i).equals(zoneArrayDB.get(i)), equalTo(true));
        }

        zoneDAO.delete(zone1);

        assertThat(zoneDAO.findByName("zone2").equals(zone2), equalTo(true));

    }
}
