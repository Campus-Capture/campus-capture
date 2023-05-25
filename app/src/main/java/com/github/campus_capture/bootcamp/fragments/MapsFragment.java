package com.github.campus_capture.bootcamp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.map.LabelInfoWindowAdapter;
import com.github.campus_capture.bootcamp.map.MapScheduler;
import com.github.campus_capture.bootcamp.map.SectionColors;
import com.github.campus_capture.bootcamp.storage.ZoneDatabase;
import com.github.campus_capture.bootcamp.storage.dao.ZoneDAO;
import com.github.campus_capture.bootcamp.storage.entities.Zone;
import com.github.campus_capture.bootcamp.utils.PermissionUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapsFragment extends Fragment implements GoogleMap.OnCameraMoveListener {

    private final int CAMERA_MOVE_DELAY = 350;
    private View localView;
    private GoogleMap map;
    private ZoneDatabase zoneDB;
    private BackendInterface backendInterface;
    private MapScheduler scheduler;

    private Map<Polygon, Marker> zoneLabels;
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

                backendInterface.attackZone(User.getUid(), User.getSection(), currentZone.getName())
                        .thenAccept(result -> {
                            if (result) {
                                Toast.makeText(v.getContext(), getString(R.string.vote_zone_toast), Toast.LENGTH_SHORT).show();
                                scheduler.confirmAttack();
                            } else {
                                Toast.makeText(v.getContext(), getString(R.string.op_failed_toast_text), Toast.LENGTH_SHORT).show();
                            }
                        }).exceptionally( e -> {
                            // TODO handle errors better ?
                            Log.e("MapFragment", "Error occurred when voting");
                            return null;
                        });
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

    private Map<String, Polygon> polygonMap;
    @SuppressLint("PotentialBehaviorOverride")
    private final OnMapReadyCallback callback = googleMap -> {

        ZoneDAO zoneDAO = zoneDB.zoneDAO();

        map = googleMap;
        map.setOnCameraMoveListener(this);
        UiSettings mapUiSettings = map.getUiSettings();

        map.setMaxZoomPreference(18);
        map.setMinZoomPreference(16);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.no_poi_style));
        View compassButton = this.getView().findViewWithTag("GoogleMapCompass");
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) compassButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        /*View locationButton = ((View) this.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 180, 180, 0);*/

        int bottomMargin = Math.round((40 * this.getContext().getResources().getDisplayMetrics().density));
        int leftMargin = Math.round((5 * this.getContext().getResources().getDisplayMetrics().density));
        rlp.setMargins(leftMargin, 0, 0, bottomMargin);

        enableMyLocation();

        // Move the camera to the campus
        LatLng epfl = new LatLng(46.520536, 6.568318);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 17));
        polygonMap = new HashMap<>();
        map.setInfoWindowAdapter(new LabelInfoWindowAdapter(getContext()));

        zoneLabels = new HashMap<>();

        for (Zone zone : zoneDAO.getAll()){
            Polygon poly = map.addPolygon(new PolygonOptions().addAll(zone.getVertices()));
            poly.setStrokeWidth(0);
            poly.setFillColor(getContext().getColor(R.color.none_color));
            polygonMap.put(zone.getName(), poly);
            poly.setClickable(true);

            zoneLabels.put(
                    poly,
                    map.addMarker(new MarkerOptions()
                            .position(zone.getCenter())
                            .icon(createPureTextIcon(zone.getName()))
                            .title(zone.getName())
                    ));
        }
        map.setOnMarkerClickListener(marker -> {
            refreshCurrentAttacks(marker, map);
            return true;
        });

        map.setOnPolygonClickListener(polygon ->{

            Marker m = zoneLabels.get(polygon);
            if(m != null)
            {

                refreshCurrentAttacks(m, map);
                zoneLabels.replace(polygon, m);
            }
            else
            {
                Log.e("MapsFragment", "Marker not found!");
            }
        });

        scheduler.startColorRefresh();
    };

    /**
     * Private method to refresh the current attacks / owner on the label of any of the maps' markers
     * @param m the marker to be affected
     * @param map the google maps instance to move the camera
     */
    private void refreshCurrentAttacks(Marker m, GoogleMap map)
    {
        Section zoneOwner = scheduler.getCurrentZoneOwner(m.getTitle());
        String displayOwner = (zoneOwner == null) ? "None" : zoneOwner.toString();
        if(scheduler.isTakeover())
        {
            backendInterface.getCurrentAttacks(m.getTitle()).thenAccept(attacks -> {
                if(!attacks.isEmpty())
                {
                    StringBuilder indicator = new StringBuilder("Current owner: ");
                    indicator.append(displayOwner);
                    indicator.append("<br>Current attacks:<br>");
                    for (Map.Entry<Section, Integer> entry : attacks.entrySet())
                    {
                        if (entry.getValue() > 0) {
                            indicator.append(entry.getKey().toString())
                                    .append(": ")
                                    .append(entry.getValue())
                                    .append("<br>");
                        }
                    }
                    m.setSnippet(indicator.toString());
                }
            }).exceptionally(e -> {
                Log.e("MapsFragment", "Failed retrieving the current attacks for the zone " + m.getTitle());
                return null;
            });
        }
        else
        {
            m.setSnippet("Current owner: " + displayOwner);
        }
        m.showInfoWindow();
        map.animateCamera(CameraUpdateFactory.newLatLng(m.getPosition()), CAMERA_MOVE_DELAY, null);
    }

    /**
     * Creates a custom icon with a text
     * @param text the text to be inserted
     */
    public BitmapDescriptor createPureTextIcon(String text) {

        Paint textPaint = new Paint();

        textPaint.setTextSize(30f); //TODO less arbitrary value (dynamic). To do in another issue

        float textWidth = textPaint.measureText(text);
        float textHeight = textPaint.getTextSize();
        int width = (int) (textWidth);
        int height = (int) (textHeight);

        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);

        canvas.translate(0, height);

        canvas.drawText(text, 0, 0, textPaint);
        return BitmapDescriptorFactory.fromBitmap(image);
    }

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

        localView = view;

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

    @Override
    public void onCameraMove() {
        CameraPosition cp = map.getCameraPosition();
        for (Marker label : zoneLabels.values()){
            label.setVisible(cp.zoom > 15);
        }
    }
    /**
     * Method to refresh the colors of the zones of the map according to their owners
     * @param zoneState The map from zone name to section
     */
    public void refreshZoneColors(Map<String, Section> zoneState)
    {
        Log.i("MapsFragment", "Refreshing zone colours");
        if(zoneState == null || polygonMap == null)
        {
            Log.e("MapsFragment", "Error: empty zone state returned");
            return;
        }
        for(String name : zoneState.keySet())
        {
            Log.i("MapsFragment", "Zone " + name);
            Section s = zoneState.get(name);
            if(s == null)
            {
                s = Section.NONE;
            }
            Polygon p = polygonMap.get(name);
            if(p == null)
            {
                Log.e("MapsFragment", "Error: zone with name " + name + " not found in map");
                continue;
            }
            p.setFillColor(SectionColors.getColor(s, getContext()));
        }
    }

}