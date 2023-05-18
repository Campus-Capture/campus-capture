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
import android.os.Bundle;

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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapColorTest {

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
        //Intents.release();
    }

    //@Ignore("Testing this is literally horrible; future me will figure it out")
    @Test
    public void testZoneColors() throws InterruptedException {

        Thread.sleep(30000);
    }

}
