package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.github.campus_capture.bootcamp.authentication.Section.*;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static java.lang.System.out;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;
import com.github.campus_capture.bootcamp.map.MapScheduler;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.checkerframework.checker.units.qual.C;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapColorTest {

    private static final double DELTA_VAL = 1;
    private boolean caught;

    private final BackendInterface mock = new PlaceholderBackend()
    {
        @Override
        public CompletableFuture<Map<String, Section>> getCurrentZoneOwners()
        {
            caught = true;
            CompletableFuture<Map<String, Section>> out = new CompletableFuture<>();
            out.completeExceptionally(new RuntimeException("Oh no"));
            return out;
        }
    };

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @BeforeClass
    public static void initClass()
    {
        MainActivity.backendInterface = new PlaceholderBackend();
    }

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
    public void runThroughFailedZoneOwners() throws InterruptedException {
        MainActivity.backendInterface = mock;
        caught = false;

        Intents.init();

        Context context = getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(3000);

        assertTrue(caught);
    }

    //TODO: Revolve in issue #153
    @Ignore("To be (maybe) tested in issue #153")
    @Test
    public void testZoneColors() {

        // Note: this test currently doesn't work, just because I am out of energy to tackle this.
        // The gist is to take the maps view-port, export it to a bitmap, and then read the
        // individual pixels of the generated image to check that the colors of the displayed
        // zones are correct. Unfortunately, it doesn't work, and for now I didn't find any way
        // of retrieving the image somehow to make out what is actually going on when the test
        // is run. Oops!

        //Thread.sleep(6000);
        /*
        testRule.getScenario().onActivity(a -> {
            View v = a.findViewById(R.id.map);
            Bitmap b = getBitmapFromView(v);

            // Testing every color is stupid, as such we'll be checking the three in the viewport

            // MX:
            int pixel = b.getPixel(150,975);
            Color color = Color.valueOf(pixel);
            assertEquals(color.red(), 253, DELTA_VAL);
            assertEquals(color.green(), 216, DELTA_VAL);
            assertEquals(color.blue(), 187, DELTA_VAL);

            // SIE:
            pixel = b.getPixel(438,1005);
            color = Color.valueOf(pixel);
            assertEquals(color.red(), 206, DELTA_VAL);
            assertEquals(color.green(), 253, DELTA_VAL);
            assertEquals(color.blue(), 247, DELTA_VAL);

            // MX:
            pixel = b.getPixel(881,936);
            color = Color.valueOf(pixel);
            assertEquals(color.red(), 206, DELTA_VAL);
            assertEquals(color.green(), 138, DELTA_VAL);
            assertEquals(color.blue(), 188, DELTA_VAL);
        });*/
    }

    // Helper method, taken from https://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

}
