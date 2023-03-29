package com.github.campus_capture.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.TextView;

import androidx.room.Room;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.FirebaseInterface;
import com.github.campus_capture.bootcamp.fragments.MapsFragment;
import com.github.campus_capture.bootcamp.map.MapScheduler;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.github.campus_capture.bootcamp.storage.ZoneDatabase;
import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class MapVoteTest {

    String recordedZoneName;

    final FirebaseInterface mock = new FirebaseInterface() {
        @Override
        public boolean voteZone(String uid, Section s, String zonename) {
            recordedZoneName = zonename;
            return true;
        }

        @Override
        public Map<String, Section> getCurrentZoneOwners() {
            Map<String, Section> out = new HashMap<>();
            out.put("campus", Section.IN);
            return out;
        }

        @Override
        public List<ScoreItem> getScores() {
            return null;
        }
    };

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void correctDisplayWhenPositionIsUnknown() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = null;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nUnknown"))));

        Intents.release();
    }

    @Test
    public void correctDisplayWhenOutsideOfZones() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(0, 0);

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nNone"))));

        Intents.release();
    }

    @Test
    public void zoneIsDisplayedWhenInsideZone() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\ncampus"))));

        Intents.release();
    }

    @Test
    public void timerbuttonIsDisplayedWhenOutsideOfTakeover() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(withText(containsString("Next takeover in"))));

        Intents.release();
    }

    @Test
    public void timerButtonCorrectlyCountsDownTime() throws ParseException, InterruptedException {
        MainActivity.firebaseInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        String comp1 = getText(ViewMatchers.withId(R.id.timerButton));
        Date date1 = new SimpleDateFormat("mm:ss").parse(comp1.substring(17));
        assert date1 != null;
        String comp2 = "Next takeover in " + new SimpleDateFormat("mm:ss").format(new Date(date1.getTime() - 5000));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(withText(containsString(comp2))));

        Intents.release();
    }

    @Test
    public void attackButtonIsDisplayedDuringTakeover() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(isClickable()));

        Intents.release();
    }

    @Test
    public void attackButtonIsDisabledOnAttack() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());

        assertEquals("campus", recordedZoneName);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));

        Intents.release();
    }

    @Test
    public void defendButtonIsDisplayedDuringTakeover() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        User.setSection(Section.IN);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(isClickable()));

        Intents.release();
    }

    @Test
    public void defendButtonIsDisabledOnAttack() throws InterruptedException {
        MainActivity.firebaseInterface = mock;
        User.setSection(Section.IN);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = new LatLng(46.520544, 6.567825);
        MapsFragment.zoneDB = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        ZoneDatabase.class, "zones-db")
                .createFromAsset("databases/zones-db.db")
                .allowMainThreadQueries().build();

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.defendButton)).perform(ViewActions.click());

        assertEquals("campus", recordedZoneName);

        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));

        Intents.release();
    }

    /**
     * Helper method to retrieve the text from a text view, since the time between opening the UI
     * and reading the value differs from the time instance injected
     * Taken from <a href="https://stackoverflow.com/questions/8833399/changing-java-date-one-hour-back">this SO post</a>
     * @param matcher the view we're trying to match
     * @return String
     */
    String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }
}
