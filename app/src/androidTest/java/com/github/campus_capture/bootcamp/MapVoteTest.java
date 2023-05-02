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

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.fragments.MapsFragment;
import com.github.campus_capture.bootcamp.map.MapScheduler;
import com.github.campus_capture.bootcamp.map.ScheduleConstants;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
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
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class MapVoteTest {

    String recordedZoneName;
    LatLng fixed = new LatLng(46.518465, 6.561907);
    LatLng noLoc = new LatLng(0, 0);

    private static boolean hasAttacked;
    private static boolean voteReturnCode;

    private final BackendInterface mock = new BackendInterface() {

        @Override
        public CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename) {
            recordedZoneName = zonename;
            return CompletableFuture.completedFuture(true);
        }

        @Override
        public CompletableFuture<Boolean> hasAttacked(String uid) {
            return CompletableFuture.completedFuture(hasAttacked);
        }

        @Override
        public CompletableFuture<Map<String, Section>> getCurrentZoneOwners() {
            Map<String, Section> out = new HashMap<>();
            out.put("BC", Section.IN);
            return CompletableFuture.completedFuture(out);
        }

        @Override
        public CompletableFuture<List<ScoreItem>> getScores() {
            return null;
        }
    };

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Before
    public void init()
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @After
    public void close()
    {
        Intents.release();
    }

    @Test
    public void correctDisplayWhenPositionIsUnknown() throws InterruptedException {
        MainActivity.backendInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = null;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nUnknown"))));
    }

    @Test
    public void correctDisplayWhenOutsideOfZones() throws InterruptedException {
        MainActivity.backendInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = noLoc;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nNone"))));
    }

    @Test
    public void zoneIsDisplayedWhenInsideZone() throws InterruptedException {
        MainActivity.backendInterface = mock;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.currentZoneText)).check(matches(withText(containsString("Current zone:\nBC"))));
    }

    @Test
    public void timerbuttonIsDisplayedWhenOutsideOfTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(withText(containsString("Next takeover in"))));
    }

    @Test
    public void timerButtonCorrectlyCountsDownTime() throws ParseException, InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        String comp1 = getText(ViewMatchers.withId(R.id.timerButton));
        Date date1 = new SimpleDateFormat("mm:ss").parse(comp1.substring(17));
        assert date1 != null;
        String comp2 = "Next takeover in " + new SimpleDateFormat("mm:ss").format(new Date(date1.getTime() - 3000));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(withText(containsString(comp2))));
    }

    @Test
    public void attackButtonIsDisplayedDuringTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(isClickable()));
    }

    @Test
    public void attackButtonIsDisabledOnAttack() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;
        voteReturnCode = true;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());

        assertEquals("BC", recordedZoneName);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void defendButtonIsDisplayedDuringTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.IN);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(isClickable()));
    }

    @Test
    public void defendButtonIsDisabledOnAttack() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.IN);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;
        voteReturnCode = true;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.defendButton)).perform(ViewActions.click());

        assertEquals("BC", recordedZoneName);

        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));

        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfButtonsAreHiddenWhenInSpectatorMode() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setUid(null);
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testIfAttackButtonIsShownAtStartOfTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 59);
        time.set(Calendar.SECOND, 55);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testIfDefendButtonIsShownAtStartOfTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.IN);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 59);
        time.set(Calendar.SECOND, 55);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testIfAttackButtonIsCorrectlyClosedAtEndOfTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 14);
        time.set(Calendar.SECOND, 55);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(not(isDisplayed())));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfDefendButtonIsCorrectlyClosedAtEndOfTakeover() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.IN);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 14);
        time.set(Calendar.SECOND, 55);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(not(isDisplayed())));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfAttackButtonsAreHiddenWhenAlreadyAttacked() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = true;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.timerButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testIfTasksAreCorrectlyClosed() throws InterruptedException {
        MainActivity.backendInterface = mock;
        MapScheduler.overrideTime = false;
        MapsFragment.locationOverride = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_rules)).perform(ViewActions.click());
    }

    @Test
    public void testThatToastIsDisplayedIfLocationIsNull() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;
        voteReturnCode = true;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(2000);

        MapsFragment.fixedLocation = null;

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());
    }

    @Test
    public void checkThatZonesAreWellRefreshed() throws InterruptedException {
        MainActivity.backendInterface = mock;
        MapScheduler.overrideTime = false;
        MapsFragment.locationOverride = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(ScheduleConstants.ZONE_REFRESH_RATE + 3000);
    }

    @Test
    public void checkThatToastIsShownWhenZoneOwnerNotFound() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;
        voteReturnCode = true;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        MapsFragment.fixedLocation = new LatLng(46.518541590052145, 6.56854108037592);

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());
    }

    @Test
    public void testThatToastIsDisplayedIfVoteFails() throws InterruptedException {
        MainActivity.backendInterface = mock;
        User.setSection(Section.SC);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = fixed;
        User.setUid("");
        hasAttacked = false;
        voteReturnCode = false;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());
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
