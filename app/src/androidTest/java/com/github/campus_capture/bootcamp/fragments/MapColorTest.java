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

import android.content.Context;
import android.content.Intent;

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
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapColorTest {

    private final BackendInterface mockColor = new PlaceholderBackend() {
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

    @Ignore("Testing this is literally horrible; future me will figure it out")
    @Test
    public void testZoneColors() throws InterruptedException {
        // TODO write some actual tests
        // Meanwhile: don't touch my spaghet

        MainActivity.backendInterface = mockColor;

        Intents.init();

        Thread.sleep(5000);

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Thread.sleep(5000);
    }

}
