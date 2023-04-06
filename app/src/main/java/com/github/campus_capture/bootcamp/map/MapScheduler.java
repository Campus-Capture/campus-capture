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
    private final Button timerButton;
    private final BackendInterface backendInterface;
    private final MapsFragment upper;
    private final Handler scheduledTaskHandler;
    private boolean isZoneOwned;
    private boolean isTakeover;
    private boolean hasAttacked;
    private Map<String, Section> zoneState;
    private CountDownTimer buttonTimer;
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
                    isZoneOwned = zoneState.get(zoneName) == User.getSection();
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
                    .thenApply( (result) -> (zoneState = result) )
                    .exceptionally( e -> {
                        Log.e("MapScheduler", "Error ocurred when retrieving the zone owners");
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
        timerButton = view.findViewById(R.id.timerButton);

        // TODO not sure how to correctly refactor this using the new future interface
        try{
            zoneState = backendInterface.getCurrentZoneOwners().get();

        } catch(Exception e){}
        // ----------------

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
     * Method to start the processes
     */
    public void startAll() {
        long millisSinceHour = time.get(Calendar.MINUTE) * MILLIS_PER_MIN
                + time.get(Calendar.SECOND) * MILLIS_PER_SEC
                + time.get(Calendar.MILLISECOND);

        Log.i("MapsFragment", "Millis since hour: " + millisSinceHour);

        buttonTimer = createTimer(timerButton, millisSinceHour);
        buttonTimer.start();

        if(millisSinceHour < TAKEOVER_DURATION)
        {
            Log.i("MapsFragment", "Started in takeover");
            isTakeover = true;
            scheduledTaskHandler.postDelayed(closeAttacksTask, TAKEOVER_DURATION - millisSinceHour);
        }
        else
        {
            Log.i("MapsFragment", "Started outside of takeover");
            isTakeover = false;
            scheduledTaskHandler.postDelayed(openAttacksTask, (MILLIS_PER_HOUR - millisSinceHour));
        }
        backendInterface.hasAttacked(User.getUid()).thenApply( (result) -> {
            hasAttacked = result;
            zoneRefreshTask.run();
            return null;
        });

    }

    /**
     * Method to stop all the running tasks
     */
    public void stopAll()
    {
        scheduledTaskHandler.removeCallbacksAndMessages(null);
        if(buttonTimer != null)
        {
            buttonTimer.cancel();
        }
    }

    /**
     * Private method to hide all the buttons (when no zone is found for ex.)
     */
    private void hideButtons()
    {
        attackButton.setVisibility(GONE);
        defendButton.setVisibility(GONE);
        timerButton.setVisibility(GONE);
    }

    /**
     * Private method to update which buttons are displayed depending on time and if the player
     * already attacked
     */
    private void showButtons()
    {
        if(User.getUid() == null)
        {
            hideButtons();
        }
        else if(isTakeover && !hasAttacked)
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
     * Private method to create the timer for the timer button
     * @param button the button instance
     * @param hourDelta the amount of time which has gone by since the last hour
     * @return the timer
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
                        upper.getString(R.string.wait_button_text) +
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
}
