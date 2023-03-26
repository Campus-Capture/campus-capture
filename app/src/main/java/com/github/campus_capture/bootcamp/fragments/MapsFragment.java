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
import android.os.Handler;
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
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.FirebaseInterface;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapsFragment extends Fragment{

    private GoogleMap map;
    private CountDownTimer buttonTimer; // The countdown on the next takeover
    private static final long MILLIS_PER_HOUR = 36000000;
    private static final long MILLIS_PER_MIN = 60000;
    private static final long MILLIS_PER_SEC = 1000; // Yes, this is obvious as ship
    private ZoneDatabase zoneDB;
    private Handler scheduledTaskHandler; // The handler with all the timed tasks (except for countdown)
    private TextView zoneText; // The textview containing the zone information
    private Button attackButton;
    private Button defendButton;
    private Button timerButton;
    private FirebaseInterface backendInterface;
    private boolean isZoneOwned;
    private boolean isTakeover;
    private boolean hasAttacked;
    private Map<String, Section> zoneState;

    /**
     * The task to refresh the current zone every ZONE_REFRESH_RATE
     */
    private final Runnable zoneRefreshTask = new Runnable() {
        @Override
        public void run() {
            String label = getString(R.string.current_zone_text);
            LatLng position = getCurrentPosition();
            if(position == null)
            {
                label += "Unknown";
                hideButtons();
            }
            else
            {
                Zone currentZone = findCurrentZone(position);
                if(currentZone == null)
                {
                    label += "None";
                    hideButtons();
                }
                else
                {
                    String zoneName = currentZone.getName();
                    label += zoneName;
                    isZoneOwned = zoneState.get(zoneName) == User.getSection();
                    showButtons();
                }
            }
            zoneText.setText(label);
            scheduledTaskHandler.postDelayed(zoneRefreshTask, ZONE_REFRESH_RATE);
            Log.i("MapsFragment", "Refreshing current zone");
        }
    };

    /**
     * The task to deactivate the attack buttons once the take over is over
     */
    private final Runnable closeAttacksTask = new Runnable() {
        @Override
        public void run() {
            isTakeover = false;
            hasAttacked = false;
            showButtons();
            scheduledTaskHandler.postDelayed(closeAttacksTask, MILLIS_PER_HOUR);
            scheduledTaskHandler.postDelayed(refreshZoneOwners,OWNER_REFRESH_DELAY);
        }
    };

    /**
     * A task to refresh the zone ownerships, to give some time to the back-end to calculate the
     * new zone owners
     */
    private final Runnable refreshZoneOwners = new Runnable() {
        @Override
        public void run() {
            zoneState = backendInterface.getCurrentZoneOwners();
        }
    };
    private static final long OWNER_REFRESH_DELAY = 5 * MILLIS_PER_SEC;

    private static final long ZONE_REFRESH_RATE = 5 * MILLIS_PER_SEC; // The refresh rate of the current zone
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

    // Mandatory empty constructor
    public MapsFragment(){}

    /**
     * Overloaded constructor to allow the fragment to use a specific back-end
     * @param backend the backend to be injected
     */
    public MapsFragment(FirebaseInterface backend)
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

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        attackButton = view.findViewById(R.id.attackButton);
        defendButton = view.findViewById(R.id.defendButton);
        timerButton = view.findViewById(R.id.timerButton);

        setButtonBehavior();

        Calendar now = Calendar.getInstance();
        long millisSinceHour =
                now.get(Calendar.MINUTE) * MILLIS_PER_MIN
                + now.get(Calendar.SECOND) * MILLIS_PER_SEC
                + now.get(Calendar.MILLISECOND);
        buttonTimer = createTimer(timerButton, millisSinceHour);
        buttonTimer.start();

        isTakeover = (now.get(Calendar.MINUTE) <= 15);
        if(isTakeover)
        {
            // TODO fix this some other way
            // this means that everytime the fragment is reloaded, the player is treated as if they
            // hadn't attacked yet
            hasAttacked = false;
        }

        long timeUntilClosure = ((15 * MILLIS_PER_MIN - millisSinceHour) % MILLIS_PER_HOUR);
        scheduledTaskHandler.postDelayed(closeAttacksTask, timeUntilClosure);
        TextView zoneText = view.findViewById(R.id.currentZoneText);
        startZoneTracking(zoneText);
    }

    @Override
    public void onDestroyView() {
        buttonTimer.cancel();
        scheduledTaskHandler.removeCallbacks(zoneRefreshTask);
        scheduledTaskHandler.removeCallbacks(closeAttacksTask);
        scheduledTaskHandler.removeCallbacks(refreshZoneOwners);
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
        return new CountDownTimer(MILLIS_PER_HOUR - hourDelta, 1000) {
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
     * Method which finds in which zone a given point is
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
            Log.i("MapsFragment", "Failed retrieving location");
        }
        return null;
    }

    /**
     * Method to start the zone tracking, and will update the current zone name in the text view
     * @param text The textview where the zone name should be displayed
     */
    private void startZoneTracking(TextView text)
    {
        zoneText = text;
        scheduledTaskHandler = new Handler();
        zoneRefreshTask.run();
    }

    /**
     * Method to show the buttons in the UI
     */
    private void showButtons()
    {
        if(isTakeover && !hasAttacked)
        {
            attackButton.setVisibility((isZoneOwned) ? GONE : VISIBLE);
            defendButton.setVisibility((isZoneOwned) ? VISIBLE : GONE);
            timerButton.setVisibility(GONE);
        }
        else
        {
            attackButton.setVisibility(GONE);
            defendButton.setVisibility(GONE);
            timerButton.setVisibility(VISIBLE);
        }
    }

    /**
     * Method to hide all buttons in the UI
     */
    private void hideButtons()
    {
        attackButton.setVisibility(GONE);
        defendButton.setVisibility(GONE);
        timerButton.setVisibility(GONE);
    }

    /**
     * Method to set the buttons' behaviors (they are the same for both the attack and defend button)
     */
    private void setButtonBehavior()
    {
        View.OnClickListener attackListener = v ->
        {
            LatLng currentPosition = getCurrentPosition();
            if(currentPosition == null)
            {
                Toast.makeText(v.getContext(), "Failed to retrieve position", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Zone currentZone = findCurrentZone(currentPosition);
                if(currentZone != null)
                {
                    // Yes, this uses Futures. Shame on you if you didn't pay attention during ParaConc
                    CompletableFuture<Boolean> voteFuture = CompletableFuture.supplyAsync(() -> backendInterface.voteZone(User.getUid(), User.getSection(), currentZone.getName()));
                    boolean result = false;
                    try
                    {
                        result = voteFuture.get();
                    }
                    catch(Exception e)
                    {
                        Log.e("MapsFragment", "Error occurred when voting for a zone", e);
                    }
                    if(result)
                    {
                        String toastText = (isZoneOwned) ? "Defending zone " : "Attacking zone ";
                        Toast.makeText(v.getContext(), toastText + currentZone.getName(), Toast.LENGTH_SHORT).show();
                        attackButton.setVisibility(GONE);
                        defendButton.setVisibility(GONE);
                        timerButton.setVisibility(VISIBLE);
                        hasAttacked = true;
                    }
                    else
                    {
                        Toast.makeText(v.getContext(), "Operation failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        attackButton.setOnClickListener(attackListener);
        defendButton.setOnClickListener(attackListener);
    }

}