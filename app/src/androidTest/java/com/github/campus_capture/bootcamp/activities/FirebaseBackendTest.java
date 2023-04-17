package com.github.campus_capture.bootcamp.activities;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.github.campus_capture.bootcamp.firebase.PlaceholderFirebaseInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class FirebaseBackendTest {

    static FirebaseDatabase database = null;

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void init_firebase_emulator() {

        try{
            AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
            database = context.getFirebaseDB();
            database.useEmulator("10.0.2.2", 9000);

            //check for connection
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        Log.d(TAG, "connected");
                    } else {
                        Log.d(TAG, "not connected");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Listener was cancelled");
                }
            });

        } catch (Exception e) {
            Log.e("Error init DB", e.toString());
        }
    }

    @Before
    public void clear_firebase_database(){
        database.getReference().setValue(null);
    }

    @Test
    public void testVoteZoneReturnsCorrect()
    {
        // TODO set database content

        BackendInterface b = new FirebaseBackend();

        try {
            assertTrue(b.voteZone("", Section.IN, "").get());
        }catch(Exception e){
            assertTrue(false);
        }

        // TODO check database content
    }

    @Test
    public void testCurrentZoneOwners()
    {
        // TODO set database content

        BackendInterface b = new FirebaseBackend();

        try{
            Map<String, Section> owners = b.getCurrentZoneOwners().get();
            assertEquals(owners.get("campus"), Section.IN);
        }catch(Exception e){
            assertTrue(false);
        }

        // TODO check database content
    }

    @Test
    public void testScoresAreWellOrdered()
    {
        // TODO set database content

        BackendInterface b = new FirebaseBackend();

        try{
            List<ScoreItem> scores = b.getScores().get();

            for(int i = 0; i < scores.size() - 1; i++)
            {
                assertTrue(scores.get(i).getValue() >= scores.get(i + 1).getValue());
            }
        }catch(Exception e){
            assertTrue(false);
        }

        // TODO check database content

    }

    @Test
    public void testIfPlayerAlreadyAttacked()
    {
        // TODO set database content

        BackendInterface b = new FirebaseBackend();

        try{
            assertFalse(b.hasAttacked("kek").get());
        }catch(Exception e){
            assertTrue(false);
        }

        // TODO check database content

    }
}
