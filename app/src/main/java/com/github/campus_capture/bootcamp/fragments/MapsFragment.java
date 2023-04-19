package com.github.campus_capture.bootcamp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.map.MapScheduler;
import com.github.campus_capture.bootcamp.storage.ZoneDatabase;
import com.github.campus_capture.bootcamp.storage.dao.ZoneDAO;
import com.github.campus_capture.bootcamp.storage.entities.Zone;
import com.github.campus_capture.bootcamp.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MapsFragment extends Fragment{

    private GoogleMap map;
    private ZoneDatabase zoneDB;
    private BackendInterface backendInterface;
    private MapScheduler scheduler;
    public static boolean locationOverride = false;
    public static LatLng fixedLocation = null;
    private final View.OnClickListener attackListener = v ->
    {
        LatLng currentPosition = getCurrentPosition();
        if(currentPosition == null)
        {
            Toast.makeText(v.getContext(), getText(R.string.position_not_found_text), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Zone currentZone = findCurrentZone(currentPosition);

            if(currentZone != null) {
/*
                backendInterface.voteZone(User.getUid(), User.getSection(), currentZone.getName())
                        .thenApply(result -> {
                            if (result) {
                                Toast.makeText(v.getContext(), getString(R.string.vote_zone_toast), Toast.LENGTH_SHORT).show();
                                scheduler.confirmAttack();
                            } else {
                                Toast.makeText(v.getContext(), getString(R.string.op_failed_toast_text), Toast.LENGTH_SHORT).show();
                            }
                            return null;
                        }).exceptionally(e -> {
                            System.out.println(e.getCause()); // returns a throwable back
                            return null;
                        });

 */
            }
            else
            {
                Toast.makeText(v.getContext(), getString(R.string.no_zone_toast_text), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied
     */
    private boolean permissionDenied = false;
    private final OnMapReadyCallback callback = googleMap -> {
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 15));

        map.setOnMarkerClickListener(marker -> {
            return false;
        });

        for (Zone zone : zoneDAO.getAll()){
            Polygon poly = map.addPolygon(new PolygonOptions().addAll(zone.getVertices()));
            poly.setStrokeWidth(0);
            poly.setFillColor(Color.argb(25, 255, 0, 0));
        }

        map.setOnPolygonClickListener(polygon ->{
        });

        // scheduler.startAll();
    };

    /**
     * Overloaded constructor to allow the fragment to use a specific back-end
     * @param backend the backend to be injected
     */
    public MapsFragment(BackendInterface backend)
    {
        backendInterface = backend;
    }

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

        zoneDB = Room.databaseBuilder(getActivity(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        scheduler = new MapScheduler(view, backendInterface, this);

        view.findViewById(R.id.attackButton).setOnClickListener(attackListener);
        view.findViewById(R.id.defendButton).setOnClickListener(attackListener);

        scheduler.startAll();
    }

    @Override
    public void onDestroyView() {
        scheduler.stopAll();
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
     * Method which finds in which zone a given point is
     * @param position the position
     * @return the (first) zone which matches the position in the DB, or null if none
     */
    public Zone findCurrentZone(LatLng position)
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
    public LatLng getCurrentPosition()
    {
        if(!locationOverride)
        {
            Location loc = null;
            try
            {
                if (!permissionDenied)
                {
                    loc = map.getMyLocation();
                }
            }
            catch (Exception e)
            {
                Log.e("MapsFragment", "Failed retrieving location");
            }
            return (loc == null) ? null : new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        else
        {
            return fixedLocation;
        }
    }

}