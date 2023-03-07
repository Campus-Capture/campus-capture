package com.github.Jenjamin3000.bootcamp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private OnMapReadyCallback callback = googleMap -> {
        // Add a marker at Satellite and move the camera
        LatLng epfl = new LatLng(46.520536, 6.568318);
        LatLng satellite = new LatLng(46.520544, 6.567825);
        googleMap.addMarker(new MarkerOptions().position(satellite).title("Satellite").snippet("5 ⭐"));

        LatLngBounds epflBounds = new LatLngBounds(
                new LatLng(46, 6), // SW bounds
                new LatLng(47, 7)  // NE bounds
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 15));

        googleMap.setOnMarkerClickListener(marker -> {
            // Triggered when user click any marker on the map
            // See example below:
           /* Toast.makeText(getActivity(),
                    marker.getTitle(),
                    Toast.LENGTH_SHORT).show();

            */
            return false;
        });

        Polygon campus = googleMap.addPolygon(new PolygonOptions()
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

        googleMap.setOnPolygonClickListener(polygon ->{
            // Triggered when user click any polygon on the map
            // See example below:
            /*Toast.makeText(getActivity(),
                    polygon.getTag().toString(),
                    Toast.LENGTH_SHORT).show();
                    */
        });
    };

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