package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static com.github.campus_capture.bootcamp.authentication.Section.AR;
import static com.github.campus_capture.bootcamp.authentication.Section.CGC;
import static com.github.campus_capture.bootcamp.authentication.Section.EL;
import static com.github.campus_capture.bootcamp.authentication.Section.GC;
import static com.github.campus_capture.bootcamp.authentication.Section.GM;
import static com.github.campus_capture.bootcamp.authentication.Section.IN;
import static com.github.campus_capture.bootcamp.authentication.Section.MA;
import static com.github.campus_capture.bootcamp.authentication.Section.MT;
import static com.github.campus_capture.bootcamp.authentication.Section.MX;
import static com.github.campus_capture.bootcamp.authentication.Section.NONE;
import static com.github.campus_capture.bootcamp.authentication.Section.PH;
import static com.github.campus_capture.bootcamp.authentication.Section.SC;
import static com.github.campus_capture.bootcamp.authentication.Section.SIE;
import static com.github.campus_capture.bootcamp.authentication.Section.SV;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

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
import com.github.campus_capture.bootcamp.map.SectionColors;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapColorTest {

    private final BackendInterface mock = new BackendInterface() {
        @Override
        public CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename) {
            return null;
        }

        @Override
        public CompletableFuture<Boolean> hasAttacked(String uid) {
            return CompletableFuture.completedFuture(false);
        }

        @Override
        public CompletableFuture<Map<String, Section>> getCurrentZoneOwners() {
            Map<String, Section> out = new HashMap<>();
            out.put("SG1", AR);
            out.put("CO Est", GC);
            out.put("CO Ouest", SIE);
            out.put("BC", IN);
            out.put("INM INR Terasse", SC);
            out.put("INM", CGC);
            out.put("INF INJ", MA);
            out.put("MXC MXD", PH);
            out.put("MXG Terrasse", EL);
            out.put("MXE MXH", SV);
            out.put("ELA ELB", MX);
            out.put("ELL", GM);
            out.put("SV AI", MT);
            out.put("Agora", NONE);
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

    @Test
    public void testZoneColors() throws InterruptedException {
        MainActivity.backendInterface = mock;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(2000);

        // Here I have to convert the view to a bitmap, since Google Maps is actually a view containing
        // a generated image. As such, I'll target the individual pixels of the view and get the color from there
        onView(ViewMatchers.withId(R.id.map)).check((view, noViewFoundException) -> {
            Bitmap bitmap = getBitmapFromView(view);
            String currentPath = "";
            Set<String> fileSet = new HashSet<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."))) {
                for (Path path : stream) {
                    if (!Files.isDirectory(path)) {
                        fileSet.add(path.getFileName()
                                .toString());
                    }
                }
            }
            catch(Exception ignored)
            {}
            for(String s : fileSet)
            {
                currentPath += s + ",";
            }
            throw new RuntimeException(currentPath);
            /*try(FileOutputStream out = new FileOutputStream("bitmap.png"))
            {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }*/
            //Context c = InstrumentationRegistry.getInstrumentation().getContext();

            // AR
            //assertEquals(bitmap.getColor(), Color.valueOf(SectionColors.getColor(AR, c)));
        });

        Intents.release();
    }

    /**
     * Method to retrieve the bitmap from a view. "Inspired" by <a href="https://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android">this</a>
     * SO link.
     * @param view The view to be converted
     * @return Bitmap
     */
    private static Bitmap getBitmapFromView(View view) {
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
