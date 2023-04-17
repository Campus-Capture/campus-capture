package com.github.campus_capture.bootcamp.activities;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.spec.ECField;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class FirebaseBackendTest {

    static FirebaseDatabase database = null;

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
                        Log.d("MY_TAG", "connected");
                    } else {
                        Log.d("MY_TAG", "not connected");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("MY_TAG", "Listener was cancelled");
                }
            });

            //wait for connection
            TimeUnit.SECONDS.sleep(3);

        } catch (Exception e) {
            Log.e("Error init DB", e.toString());
        }
    }

    @Test
    public void dummyTest(){

        database.getReference().child("test").setValue("hello banana");
        assertTrue(true);
    }

    @Before
    public void clear_firebase_database(){

        //database.getReference().setValue(null);
    }

    @Test
    public void testVoteZone() {
        database.getReference().setValue(null);
        // set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(false);
        database.getReference().child("Zones").child("BC").child("IN").setValue(4);

        BackendInterface b = new FirebaseBackend();

        try {
            Boolean result = b.voteZone("testUserId", Section.IN, "BC").get();
            Log.d("MY_TAG", "result test vote zone " + result);
            assertTrue(result);
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            assertTrue(false);
        }

        // check database content
        CompletableFuture<Boolean> futureResultHasVoted = new CompletableFuture<>();
        database.getReference().child("Users").child("testUserId").child("has_voted").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    fail();
                }
                else {
                    futureResultHasVoted.complete( (Boolean)task.getResult().getValue() );
                }
            }
        });

        CompletableFuture<Long> futureResultVoteCount = new CompletableFuture<>();
        database.getReference().child("Zones").child("BC").child("IN").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    fail();
                }
                else {
                    futureResultVoteCount.complete( (Long)task.getResult().getValue() );
                }
            }
        });

        try{
            assertTrue(futureResultHasVoted.get());
            assertEquals(5, futureResultVoteCount.get().longValue());
        }catch (Exception e){
            fail();
        }
    }

    // TODO testVoteZoneImpossibleWhenAlreadyVoted

    @Test
    @Ignore
    public void testCurrentZoneOwners()
    {
        // TODO set database content

        BackendInterface b = new FirebaseBackend();

        try{
            Map<String, Section> owners = b.getCurrentZoneOwners().get();
            assertEquals(owners.get("campus"), Section.IN);
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            assertTrue(false);
        }

        // TODO check database content
    }

    @Test @Ignore
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
            Log.e("Error in test", e.toString());
            assertTrue(false);
        }

        // TODO check database content

    }

    @Test
    public void testIfPlayerAlreadyAttackedFalse()
    {
        // TODO set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(false);

        BackendInterface b = new FirebaseBackend();

        try{
            assertFalse(b.hasAttacked("testUserId").get());
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            assertTrue(false);
        }
    }

    @Test
    public void testIfPlayerAlreadyAttackedTrue()
    {
        // TODO set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(true);

        BackendInterface b = new FirebaseBackend();

        try{
            assertTrue(b.hasAttacked("testUserId").get());
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            assertTrue(false);
        }
    }
}
