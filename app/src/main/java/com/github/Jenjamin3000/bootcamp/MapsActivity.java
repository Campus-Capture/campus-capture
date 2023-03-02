package com.github.Jenjamin3000.bootcamp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.github.Jenjamin3000.bootcamp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;



public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener,
        GoogleMap.OnPolygonClickListener,
        OnMapReadyCallback {

    //private static final int COLOR_RED_ARGB = 0xffF9A825;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at Satellite and move the camera
        LatLng epfl = new LatLng(46.520536, 6.568318);
        LatLng satellite = new LatLng(46.520544, 6.567825);
        mMap.addMarker(new MarkerOptions().position(satellite).title("Satellite").snippet("5 ⭐"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(epfl));
        //mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        LatLngBounds epflBounds = new LatLngBounds(
                new LatLng(46, 6), // SW bounds
                new LatLng(47, 7)  // NE bounds
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 15));

        mMap.setOnMarkerClickListener(this);

        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
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

        polygon.setTag("EPFL");
        polygon.setFillColor(Color.argb(25, 255, 0, 0));
        polygon.setStrokeWidth(0);
        mMap.setOnPolygonClickListener(this);


    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Toast.makeText(this,
                marker.getTitle(),
                Toast.LENGTH_SHORT).show();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {
        Toast.makeText(this,
                polygon.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }
}