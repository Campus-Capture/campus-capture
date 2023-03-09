package com.github.Jenjamin3000.bootcamp;

import static androidx.core.app.ActivityCompat.requestPermissions;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.Jenjamin3000.bootcamp.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsFragment extends Fragment{

    private GoogleMap map;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied
     */
    private boolean permissionDenied = false;
    private OnMapReadyCallback callback = googleMap -> {
        map = googleMap;

        enableMyLocation();

        //Listener when user clicks on the "my position" button
        map.setOnMyLocationButtonClickListener(() -> {
            Toast.makeText(getActivity(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
            return false;
        });

        //Listener when user clicks on the "my position" blue dot
        map.setOnMyLocationClickListener(location -> {
            Toast.makeText(getActivity(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
        });

        // Add a marker at Satellite and move the camera
        LatLng epfl = new LatLng(46.520536, 6.568318);
        LatLng satellite = new LatLng(46.520544, 6.567825);
        map.addMarker(new MarkerOptions().position(satellite).title("Satellite").snippet("5 ⭐"));

        LatLngBounds epflBounds = new LatLngBounds(
                new LatLng(46, 6), // SW bounds
                new LatLng(47, 7)  // NE bounds
        );
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

        Polygon campus = map.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(46.5223, 6.5633),
                        new LatLng(46.5183, 6.5610),
                        new LatLng(46.5181, 6.5655),
                        new LatLng(46.5173, 6.5656),
                        new LatLng(46.5176, 6.5698),
                        new LatLng(46.5183, 6.5722),
                        new LatLng(46.5217, 6.5718),
                        new LatLng(46.5219, 6.5684),
                        new LatLng(46.5223, 6.5633)));

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
    }

}