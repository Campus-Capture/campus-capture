package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.platform.app.InstrumentationRegistry.getArguments;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;
import com.github.campus_capture.bootcamp.map.MapScheduler;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class MapLabelTest {

    private final BackendInterface mock = new PlaceholderBackend()
    {
        @Override
        public CompletableFuture<Map<String, Section>> getCurrentZoneOwners()
        {
            Map<String, Section> out = new HashMap<>();
            out.put("Sat Terrasse", Section.IN);
            out.put("MA", Section.SC);
            return CompletableFuture.completedFuture(out);
        }

        @Override
        public CompletableFuture<Map<Section, Integer>> getCurrentAttacks(String zoneName)
        {
            Map<Section, Integer> out = new HashMap<>();
            if(Objects.equals(zoneName, "Sat Terrasse"))
            {
                out.put(Section.AR, 5);
                out.put(Section.CGC, 4);
                out.put(Section.GC, 2);
            }
            else
            {
                out.put(Section.EL, 2);
                out.put(Section.MT, 1);
            }
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

    @Test
    public void openInfoWindowFromLabel() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;

        Intents.init();

        Context context = getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        Point p = new Point();
        testRule.getScenario().onActivity(a -> a.getWindowManager().getDefaultDisplay().getSize(p));
        double x = 0.400 * p.x;
        double y = 0.538 * p.y;
        device.click((int) x, (int) y);

        Thread.sleep(1000);
    }

    @Test
    public void openInfoWindowFromPolygon() throws InterruptedException {
        MainActivity.backendInterface = mock;Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 10);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        Intents.init();

        Context context = getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        Point p = new Point();
        testRule.getScenario().onActivity(a -> a.getWindowManager().getDefaultDisplay().getSize(p));
        double x = 0.389 * p.x;
        double y = 0.727 * p.y;
        device.click((int) x, (int) y);

        Thread.sleep(1000);
    }

    @Test
    public void openInfoWindowFromLabelOutsideOfAttack() throws InterruptedException {
        MainActivity.backendInterface = mock;
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;

        Intents.init();

        Context context = getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        Point p = new Point();
        testRule.getScenario().onActivity(a -> a.getWindowManager().getDefaultDisplay().getSize(p));
        double x = 0.400 * p.x;
        double y = 0.538 * p.y;
        device.click((int) x, (int) y);

        Thread.sleep(1000);
    }

    @Test
    public void openInfoWindowFromPolygonOutsideOfAttack() throws InterruptedException {
        MainActivity.backendInterface = mock;Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;
        Intents.init();

        Context context = getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        UiDevice device = UiDevice.getInstance(getInstrumentation());
        Point p = new Point();
        testRule.getScenario().onActivity(a -> a.getWindowManager().getDefaultDisplay().getSize(p));
        double x = 0.389 * p.x;
        double y = 0.727 * p.y;
        device.click((int) x, (int) y);

        Thread.sleep(1000);
    }

}
