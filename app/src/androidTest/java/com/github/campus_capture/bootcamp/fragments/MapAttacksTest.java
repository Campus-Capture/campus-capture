package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;
import com.github.campus_capture.bootcamp.map.MapScheduler;
import com.google.android.gms.maps.model.LatLng;

import org.checkerframework.checker.units.qual.C;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class MapAttacksTest {

    private final LatLng campusLocation = new LatLng(46.520040, 6.564556);
    private String recordedZoneName;
    private String recordedUID;
    private Section recordedSection;
    private boolean returnCode;
    private boolean hasAttackedInject;
    private boolean attackFail;
    private boolean registered;

    private View decorView;

    private final BackendInterface mock = new PlaceholderBackend()
    {
        @Override
        public CompletableFuture<Boolean> attackZone(String uid, Section s, String zonename)
        {
            if(attackFail)
            {
                registered = true;
                CompletableFuture<Boolean> out = new CompletableFuture<>();
                out.completeExceptionally(new RuntimeException("Oh no"));
                return out;
            }
            recordedZoneName = zonename;
            recordedUID = uid;
            recordedSection = s;
            return CompletableFuture.completedFuture(returnCode);
        }

        @Override
        public CompletableFuture<Boolean> hasAttacked(String uid)
        {
            return CompletableFuture.completedFuture(hasAttackedInject);
        }

        @Override
        public CompletableFuture<Map<String, Section>> getCurrentZoneOwners()
        {
            Map<String, Section> out = new HashMap<>();
            out.put("CO Ouest", Section.IN);
            return CompletableFuture.completedFuture(out);
        }
    };

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @After
    public void close()
    {
        Intents.release();
    }

    @Before
    public void init()
    {
        recordedZoneName = "";
        recordedUID = "";
        recordedSection = null;
        testRule.getScenario().onActivity(activity -> decorView = activity.getWindow().getDecorView());
    }

    @Test
    public void attackSucceeds() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.SC);
        hasAttackedInject = false;
        returnCode = true;
        attackFail = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());

        assertEquals(recordedZoneName, "CO Ouest");
        assertEquals(recordedUID, "Jotanus, the trusty hole rimmer");
        assertEquals(recordedSection, Section.SC);
        onView(withText("Attack registered"))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void defenseSucceeds() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.IN);
        hasAttackedInject = false;
        returnCode = true;
        attackFail = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.defendButton)).perform(ViewActions.click());

        assertEquals(recordedZoneName, "CO Ouest");
        assertEquals(recordedUID, "Jotanus, the trusty hole rimmer");
        assertEquals(recordedSection, Section.IN);
        onView(withText("Attack registered"))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void attackFailsCorrectly() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.SC);
        hasAttackedInject = false;
        returnCode = false;
        attackFail = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.attackButton)).perform(ViewActions.click());

        assertEquals(recordedZoneName, "CO Ouest");
        assertEquals(recordedUID, "Jotanus, the trusty hole rimmer");
        assertEquals(recordedSection, Section.SC);
        onView(withText("Operation failed"))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void defenseFailsCorrectly() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.IN);
        hasAttackedInject = false;
        returnCode = false;
        attackFail = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.defendButton)).perform(ViewActions.click());

        assertEquals(recordedZoneName, "CO Ouest");
        assertEquals(recordedUID, "Jotanus, the trusty hole rimmer");
        assertEquals(recordedSection, Section.IN);
        onView(withText("Operation failed"))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));
    }

    @Test
    public void attackButtonHiddenWhenAlreadyAttacked() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.SC);
        hasAttackedInject = true;
        returnCode = false;
        attackFail = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.takeoverPhaseBanner)).check(matches(isDisplayed()));
    }

    @Test
    public void defendButtonHiddenWhenAlreadyAttacked() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.IN);
        hasAttackedInject = true;
        returnCode = false;
        attackFail = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.attackButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.defendButton)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.takeoverPhaseBanner)).check(matches(isDisplayed()));
    }

    @Test
    public void attackErrorCorrectlyLogs() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        MapsFragment.locationOverride = true;
        MapsFragment.fixedLocation = campusLocation;
        User.setUid("Jotanus, the trusty hole rimmer");
        User.setSection(Section.IN);
        hasAttackedInject = false;
        returnCode = false;
        attackFail = true;
        registered = false;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        onView(ViewMatchers.withId(R.id.defendButton)).perform(ViewActions.click());

        assertTrue(registered);
    }
}
