package com.github.campus_capture.bootcamp.map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.MILLIS_PER_HOUR;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.MILLIS_PER_MIN;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.MILLIS_PER_SEC;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.NO_POSITION_RETRY_DELAY;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.OWNER_REFRESH_DELAY;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.TAKEOVER_DURATION;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.ZONE_REFRESH_RATE;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.fragments.MapsFragment;
import com.github.campus_capture.bootcamp.storage.entities.Zone;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapScheduler {

    private final TextView zoneText;
    private final Button attackButton;
    private final Button defendButton;
    private final View planningBanner;
    private final View takeoverBanner;
    private final TextView planningBannerText;
    private final TextView takeoverBannerText;
    private final BackendInterface backendInterface;
    private final MapsFragment upper;
    private final Handler scheduledTaskHandler;
    private boolean isZoneOwned;
    private boolean isTakeover;
    private boolean hasAttacked;
    private Map<String, Section> zoneState;
    private CountDownTimer planningTimer;
    private CountDownTimer takeoverTimer;
    public static boolean overrideTime = false;
    public static Calendar time;

    // Task to refresh the zone display at the top of the map
    private final Runnable zoneRefreshTask = new Runnable() {
        @Override
        public void run() {

            String label = upper.getString(R.string.current_zone_text);
            LatLng position = upper.getCurrentPosition();

            if(position == null)
            {
                label += "Unknown";
                hideButtons();
                scheduledTaskHandler.postDelayed(zoneRefreshTask, NO_POSITION_RETRY_DELAY);
            }
            else
            {
                Zone currentZone = upper.findCurrentZone(position);

                if(currentZone == null)
                {
                    label += "None";
                    isZoneOwned = false;
                    hideButtons();
                }
                else
                {
                    String zoneName = currentZone.getName();
                    label += zoneName;
                    if(zoneState != null)
                    {
                        isZoneOwned = zoneState.get(zoneName) == User.getSection();
                    }
                    showButtons();
                }
                scheduledTaskHandler.postDelayed(zoneRefreshTask, ZONE_REFRESH_RATE);
            }
            zoneText.setText(label);
            Log.i("MapsFragment", "Refreshing current zone");
        }
    };

    // Task to refresh the zone owners
    private final Runnable refreshZoneState = new Runnable() {
        @Override
        public void run()
        {
            backendInterface.getCurrentZoneOwners()
                    .thenAccept( (result) -> {
                        zoneState = result;
                        upper.refreshZoneColors(zoneState);
                        Log.i("MapScheduler", "Refreshed the zone owners");
                    }).exceptionally( e -> {
                        Log.e("MapScheduler", "Error ocurred when retrieving the zone owners:\t" + e.getMessage());
                        return null;
                    });
        }
    };

    // Task to open the attack buttons at the start of the takeover
    private final Runnable openAttacksTask = new Runnable() {
        @Override
        public void run() {
            isTakeover = true;
            hasAttacked = false;
            showButtons();
            scheduledTaskHandler.postDelayed(closeAttacksTask, TAKEOVER_DURATION);
        }
    };

    // Task to close the attack buttons once the takeover is over
    private final Runnable closeAttacksTask = new Runnable() {
        @Override
        public void run() {
            isTakeover = false;
            showButtons();
            scheduledTaskHandler.postDelayed(openAttacksTask, MILLIS_PER_HOUR - TAKEOVER_DURATION);
            scheduledTaskHandler.postDelayed(refreshZoneState, OWNER_REFRESH_DELAY);
        }
    };

    /**
     * Constructor
     * @param view the view of the fragment
     * @param backend the back-end to be used
     * @param upper the fragment above
     */
    public MapScheduler(View view, BackendInterface backend, MapsFragment upper)
    {
        backendInterface = backend;
        this.upper = upper;
        scheduledTaskHandler = new Handler();
        zoneText = view.findViewById(R.id.currentZoneText);
        attackButton = view.findViewById(R.id.attackButton);
        defendButton = view.findViewById(R.id.defendButton);
        planningBanner = view.findViewById(R.id.planningPhaseBanner);
        takeoverBanner = view.findViewById(R.id.takeoverPhaseBanner);
        planningBannerText = view.findViewById(R.id.planningBannerText);
        takeoverBannerText = view.findViewById(R.id.takeoverBannerText);

        if(!overrideTime)
        {
            time = Calendar.getInstance();
        }
    }

    /**
     * Method to confirm that an attack has been launched
     */
    public void confirmAttack()
    {
        hasAttacked = true;
        showButtons();
    }

    /**
     * Method to start the processes, except the zone color refresh (needs to wait for the map)
     */
    public void startAll() {
        long millisSinceHour = time.get(Calendar.MINUTE) * MILLIS_PER_MIN
                + time.get(Calendar.SECOND) * MILLIS_PER_SEC
                + time.get(Calendar.MILLISECOND);

        Log.i("MapsFragment", "Millis since hour: " + millisSinceHour);

        // buttonTimer = createTimer(timerButton, millisSinceHour);
        // buttonTimer.start();
        planningTimer = createPlanningTimer(millisSinceHour);
        planningTimer.start();
        takeoverTimer = createTakeoverTimer(millisSinceHour);
        takeoverTimer.start();

        if(millisSinceHour < TAKEOVER_DURATION)
        {
            Log.i("MapsFragment", "Started in takeover");
            isTakeover = true;
            scheduledTaskHandler.postDelayed(closeAttacksTask, TAKEOVER_DURATION - millisSinceHour);
            planningBanner.setVisibility(GONE);
            takeoverBanner.setVisibility(VISIBLE);
        }
        else
        {
            Log.i("MapsFragment", "Started outside of takeover");
            isTakeover = false;
            scheduledTaskHandler.postDelayed(openAttacksTask, (MILLIS_PER_HOUR - millisSinceHour));
            planningBanner.setVisibility(VISIBLE);
            takeoverBanner.setVisibility(GONE);
        }

        // TODO properly fix errors due to null uid if user not logged in
        backendInterface.hasAttacked(User.getUid()).thenAccept( (result) -> {

            // TODO tmp solution ?
            if(result != null){
                hasAttacked = result;
                refreshZoneState.run();
            }
        }).exceptionally( e -> {
            // TODO handle errors better ?
            Log.e("MapScheduler", "Error ocurred when retrieving if the user has attacked");
            return null;
        });

        zoneRefreshTask.run();
    }

    /**
     * Method to start the color refreshes
     */
    public void startColorRefresh()
    {
        refreshZoneState.run();
    }

    /**
     * Method to stop all the running tasks
     */
    public void stopAll()
    {
        scheduledTaskHandler.removeCallbacksAndMessages(null);
        planningTimer.cancel();
        takeoverTimer.cancel();
    }

    /**
     * Private method to hide all the buttons (when no zone is found for ex.)
     */
    private void hideButtons()
    {
        attackButton.setVisibility(GONE);
        defendButton.setVisibility(GONE);
    }

    /**
     * Private method to update which buttons are displayed depending on time and if the player
     * already attacked
     */
    private void showButtons()
    {
        if(isTakeover && !hasAttacked && User.getUid() != null)
        {
            attackButton.setVisibility((isZoneOwned) ? GONE : VISIBLE);
            defendButton.setVisibility((isZoneOwned) ? VISIBLE : GONE);
        }
        else
        {
            hideButtons();
        }
    }

    /**
     * Private method to create the timer for the timer button
     * @param hourDelta the amount of time which has gone by since the last hour
     */

    private CountDownTimer createPlanningTimer(long hourDelta)
    {
        return new CountDownTimer(MILLIS_PER_HOUR - hourDelta, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                @SuppressLint("SimpleDateFormat")
                String timestamp = new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished));
                planningBannerText.setText(
                        upper.getString(R.string.planningBanner) +
                                " " +
                                timestamp
                );
            }

            @Override
            public void onFinish() {
                planningBanner.setVisibility(GONE);
                takeoverBanner.setVisibility(VISIBLE);
                planningTimer = createPlanningTimer(0);
                planningTimer.start();
            }
        };
    }

    private CountDownTimer createTakeoverTimer(long hourDelta)
    {
        long stamp = TAKEOVER_DURATION - hourDelta;
        if(stamp <= 0 )
        {
            stamp += MILLIS_PER_HOUR;
        }
        return new CountDownTimer(stamp, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                @SuppressLint("SimpleDateFormat")
                String timestamp = new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished));
                takeoverBannerText.setText(
                        upper.getString(R.string.takeoverBanner) +
                                " " +
                                timestamp
                );
            }

            @Override
            public void onFinish() {
                planningBanner.setVisibility(VISIBLE);
                takeoverBanner.setVisibility(GONE);
                takeoverTimer = createTakeoverTimer(TAKEOVER_DURATION);
                takeoverTimer.start();
            }
        };
    }

    /**
     * Getter to know if we're currently in a takeover
     * @return boolean
     */
    public boolean isTakeover()
    {
        return isTakeover;
    }

    /**
     * Getter to retrieve the section owning the current zone
     * @param name the name of the zone
     * @return the section
     */
    public Section getCurrentZoneOwner(String name)
    {
        return zoneState.get(name);
    }
}
