package com.github.campus_capture.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.FirebaseInterface;
import com.github.campus_capture.bootcamp.fragments.MapsFragment;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class MapVoteTest {

    FirebaseInterface mock = new FirebaseInterface() {
        @Override
        public boolean voteZone(String uid, Section s, String zonename) {
            return false;
        }

        @Override
        public Map<String, Section> getCurrentZoneOwners() {
            return null;
            //Map<String, Section> out = new HashMap<>();
            //out.put("campus", )
        }

        @Override
        public List<ScoreItem> getScores() {
            return null;
        }
    };

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void correctDisplayWhenPositionIsUnknown()
    {
        MainActivity.firebaseInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = null;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nUnknown"))));

        Intents.release();
    }

    @Test
    public void correctDisplayWhenOutsideOfZones()
    {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MainActivity.firebaseInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(0, 0);

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nNone"))));

        Intents.release();
    }

    /*
    @Test
    public void timerButtonShowsWhenOutsideOfTakeover()
    {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);

        LatLng position = new LatLng(0., 0.);

        MainActivity.firebaseInterface = mock;
        MainActivity.time = time;
        MainActivity.fixedPosition = position;
        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());


    }
    */
}
