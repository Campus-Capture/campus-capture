package com.github.campus_capture.bootcamp.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.storage.ZoneDatabase;
import com.github.campus_capture.bootcamp.storage.dao.ZoneDAO;
import com.github.campus_capture.bootcamp.storage.entities.Zone;
import com.github.campus_capture.bootcamp.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsFragment extends Fragment{

    private GoogleMap map;
    private CountDownTimer buttonTimer;
    private static final long HOUR_MILLIS = 36000000;
    private static final long MILLIS_PER_MIN = 60000;
    private static final long MILLIS_PER_SEC = 1000; // Yes, this is obvious as ship
    private ZoneDatabase zoneDB;
    private Timer currentZoneUpdate;
    private static final long ZONE_REFRESH_RATE = 10 * MILLIS_PER_SEC; // The refresh rate of the current zone
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied
     */
    private boolean permissionDenied = false;
    private final OnMapReadyCallback callback = googleMap -> {

        //Build and initialize the DB
        //The DB contains the Zone around the campus
        zoneDB = Room.databaseBuilder(getActivity(),
                ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();
        // zoneDB = Room.databaseBuilder(getActivity(), ZoneDatabase.class, "zones-db").build();

        ZoneDAO zoneDAO = zoneDB.zoneDAO();

        map = googleMap;

        enableMyLocation();

        //Listener when user clicks on the "my position" button
        map.setOnMyLocationButtonClickListener(() -> {
            Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
            return false;
        });

        //Listener when user clicks on the "my position" blue dot
        map.setOnMyLocationClickListener(location -> Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show());

        // Add a marker at Satellite and move the camera
        LatLng epfl = new LatLng(46.520536, 6.568318);
        LatLng satellite = new LatLng(46.520544, 6.567825);
        map.addMarker(new MarkerOptions().position(satellite).title("Satellite").snippet("5 ⭐"));

        /*
        );*/
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 15));

        map.setOnMarkerClickListener(marker -> {
            // Triggered when user click any marker on the map
            // See example below:
           /* Toast.makeText(getActivity(),
                    marker.getTitle(),
                    Toast.LENGTH_SHORT).show();

            */
            return false;
        });

        if(zoneDAO.findByName("campus") == null)
        {
            List<LatLng> epflBounds = new ArrayList<>();
            epflBounds.add(new LatLng(46, 6)); // SW bounds
            epflBounds.add(new LatLng(47, 7)); // NE bounds
            Zone campusZone = new Zone("campus", epflBounds);
            zoneDAO.insertAll(campusZone);
        }

        Polygon campus = map.addPolygon(new PolygonOptions()
                .clickable(true)
                .addAll(zoneDAO.findByName("campus").getVertices()));

        campus.setTag("EPFL");
        campus.setFillColor(Color.argb(25, 255, 0, 0));
        campus.setStrokeWidth(0);

        map.setOnPolygonClickListener(polygon ->{
            // Triggered when user click any polygon on the map
            // See example below:
            /*Toast.makeText(getActivity(),
                    polygon.getTag().toString(),
                    Toast.LENGTH_SHORT).show();
                    */
        });
    };

    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        //PermissionUtils.requestPermission((MainActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE, true);
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.

            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.

            PermissionUtils.RationaleDialog.newInstance(LOCATION_PERMISSION_REQUEST_CODE, false)
                    .show(getActivity().getSupportFragmentManager(), "dialog");
            permissionDenied = true;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        Button attackButton = view.findViewById(R.id.attackButton);
        Button defendButton = view.findViewById(R.id.defendButton);
        Button timerButton = view.findViewById(R.id.timerButton);

        attackButton.setOnClickListener(v -> {
            attackButton.setVisibility(GONE);
            defendButton.setVisibility(VISIBLE);
        });

        defendButton.setOnClickListener(v -> {
            defendButton.setVisibility(GONE);
            timerButton.setVisibility(VISIBLE);
        });

        timerButton.setOnClickListener(v -> {
            timerButton.setVisibility(GONE);
            attackButton.setVisibility(VISIBLE);
        });

        Calendar now = Calendar.getInstance();
        buttonTimer = createTimer(timerButton,
                now.get(Calendar.MINUTE) * MILLIS_PER_MIN
                + now.get(Calendar.SECOND) * MILLIS_PER_SEC
                + now.get(Calendar.MILLISECOND));
        buttonTimer.start();

        TextView zoneText = view.findViewById(R.id.currentZoneText);
        startZoneTracking(zoneText);
    }

    @Override
    public void onDestroyView() {
        buttonTimer.cancel();
        currentZoneUpdate.cancel();
        zoneDB.close();
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        zoneDB.close();
        super.onDestroy();
    }

    /**
     * Method to create the timer on the wait button. Refreshes the timer every hour
     * @param button the button instance
     * @param hourDelta the time aready gone by since the last hour, in milliseconds
     */
    private CountDownTimer createTimer(Button button, long hourDelta)
    {
        return new CountDownTimer(HOUR_MILLIS - hourDelta, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                @SuppressLint("SimpleDateFormat")
                String timestamp = new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished));
                button.setText(
                    getString(R.string.wait_button_text) +
                    " " +
                    timestamp
                );
            }

            @Override
            public void onFinish() {
                buttonTimer = createTimer(button, 0);
                buttonTimer.start();
            }
        };
    }

    /**
     * Method whic finds in which zone a given point is
     * @param position the position
     * @return the (first) zone which matches the position in the DB, or null if none
     */
    private Zone findCurrentZone(LatLng position)
    {
        ZoneDAO zoneDAO = zoneDB.zoneDAO();
        List<Zone> zones = zoneDAO.getAll();
        for(Zone z : zones)
        {
            if(PolyUtil.containsLocation(position, z.getVertices(), false))
            {
                return z;
            }
        }
        return null;
    }

    /**
     * Method to get the current position of the player. Note that this uses a deprecated method
     * of map, and should use a FusedLocationProviderClient instead
     * @return LatLng or null
     */
    private LatLng getCurrentPosition()
    {
        try
        {
            if(!permissionDenied)
            {
                Location loc = map.getMyLocation();
                return new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        }
        catch (Exception e)
        {
            Log.e("Exception: %s", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Method to start the zone tracking, and will update the current zone name in the text view
     * @param text The textview where the zone name should be displayed
     */
    private void startZoneTracking(TextView text)
    {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String label = getString(R.string.current_zone_text);
                LatLng position = getCurrentPosition();
                if(position == null)
                {
                    label += "Unknown";
                }
                else
                {
                    Zone currentZone = findCurrentZone(position);
                    if(currentZone == null)
                    {
                        label += "None";
                    }
                    else
                    {
                        label += currentZone.getName();
                    }
                }
                text.setText(label);
            }
        };
        currentZoneUpdate = new Timer();
        currentZoneUpdate.scheduleAtFixedRate(task, 0, ZONE_REFRESH_RATE);
    }

}