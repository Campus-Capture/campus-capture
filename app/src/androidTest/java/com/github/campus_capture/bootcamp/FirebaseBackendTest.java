package com.github.campus_capture.bootcamp;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class FirebaseBackendTest {

    static FirebaseDatabase database = null;

    @BeforeClass
    public static void init_firebase_emulator() {

        try{
            AppContext context = AppContext.getAppContext();
            database = context.getFirebaseDB();
            Log.d("FirebaseBackendTest", "enable emulator");
            database.useEmulator("10.0.2.2", 9000);

        } catch (Exception e) {
            Log.e("FirebaseBackendTest", e.toString());
        }
    }

    @Before
    public void clear_firebase_database(){ database.getReference().setValue(null);}

    @Test
    public void testVoteZone() {
        // set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(false);
        database.getReference().child("Zones").child("BC").child("IN").setValue(4);

        BackendInterface b = new FirebaseBackend();

        try {
            Boolean result = b.voteZone("testUserId", Section.IN, "BC").get();
            assertTrue(result);
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
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

    @Test
    public void testVoteZoneImpossibleAlreadyVoted() {
        // set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(true);
        database.getReference().child("Zones").child("BC").child("IN").setValue(4);

        BackendInterface b = new FirebaseBackend();

        try {
            Boolean result = b.voteZone("testUserId", Section.IN, "BC").get();
            assertFalse(result);
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
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
            assertEquals(4, futureResultVoteCount.get().longValue());
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testVoteZoneInexistantZone()
    {
        // set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(false);

        BackendInterface b = new FirebaseBackend();

        try{
            b.voteZone("testUserId", Section.IN, "zoneasdfajsdf").get();
        }catch(Throwable e){
            if(e.toString() != "Could not get result from the database"){
                fail();
            }

        }
    }
    @Ignore
    @Test
    public void testVoteZoneInexistantPlayer()
    {
        // set database content
        database.getReference().child("Zones").child("BC").child("IN").setValue(4);
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(false);


        BackendInterface b = new FirebaseBackend();

        try{
            b.voteZone("playerIDylsdfjasdlfj", Section.IN, "zoneasdfajsdf").get();
        }catch(Throwable e){
            if(e.toString() != "Could not get result from the database"){
                fail();
            }

        }
    }

    @Test
    public void testCurrentZoneOwners()
    {
        // set database content
        DatabaseReference zonesRef = database.getReference().child("Zones");
        zonesRef.child("BC").child("owner").setValue("SV");
        zonesRef.child("CE").child("owner").setValue("IN");
        zonesRef.child("CO").child("owner").setValue("SIE");
        zonesRef.child("SG").child("owner").setValue("AR");
        zonesRef.child("INF").child("owner").setValue("SC");

        BackendInterface b = new FirebaseBackend();

        try{
            Map<String, Section> owners = b.getCurrentZoneOwners().get();
            assertEquals(Section.AR, owners.get("SG"));
            assertEquals(Section.IN, owners.get("CE"));
            assertEquals(Section.SV, owners.get("BC"));
            assertEquals(Section.SC, owners.get("INF"));
            assertEquals(Section.SIE, owners.get("CO"));

        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
        }
    }

    @Test
    public void testCurrentOwnersNoZones()
    {
        // set database content

        BackendInterface b = new FirebaseBackend();

        try{
            b.getCurrentZoneOwners().get();
        }catch(Throwable e){
            if(e.toString() != "Could not get result from the database"){
                fail();
            }

        }
    }

    @Test
    public void testScoresAreWellOrdered()
    {
        // set database content
        DatabaseReference sectionsRef = database.getReference().child("Sections");
        sectionsRef.child("IN").child("score").setValue(66);
        sectionsRef.child("SC").child("score").setValue(1);
        sectionsRef.child("EL").child("score").setValue(12);
        sectionsRef.child("GC").child("score").setValue(44);
        sectionsRef.child("MX").child("score").setValue(4);
        sectionsRef.child("MT").child("score").setValue(555);

        BackendInterface b = new FirebaseBackend();

        try{
            List<ScoreItem> scores = b.getScores().get();

            for(int i = 0; i < scores.size() - 1; i++)
            {
                assertTrue(scores.get(i).getValue() >= scores.get(i + 1).getValue());
            }
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
        }
    }

    @Test
    public void testScoreNoZones()
    {
        // set database content

        BackendInterface b = new FirebaseBackend();

        try{
            b.getScores().get();
        }catch(Throwable e){
            if(e.toString() != "Could not get result from the database"){
                fail();
            }

        }
    }

    @Test
    public void testIfPlayerAlreadyAttackedFalse()
    {
        // set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(false);

        BackendInterface b = new FirebaseBackend();

        try{
            assertFalse(b.hasAttacked("testUserId").get());
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
        }
    }

    @Test
    public void testIfPlayerAlreadyAttackedTrue()
    {
        // set database content
        database.getReference().child("Users").child("testUserId").child("has_voted").setValue(true);

        BackendInterface b = new FirebaseBackend();

        try{
            assertTrue(b.hasAttacked("testUserId").get());
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
        }
    }

    @Test
    public void testIfPlayerAlreadyAttackedInexistantPlayer()
    {
        // set database content

        BackendInterface b = new FirebaseBackend();

        try{
            b.hasAttacked("inexistant Player").get();
        }catch(Throwable e){
            if(e.toString() != "Could not get result from the database"){
                fail();
            }

        }
    }

    @Test
    public void testRegisterUserInDB() {

        BackendInterface b = new FirebaseBackend();

        try {
            Boolean result = b.initUserInDB("testUserId", Section.IN).get();
            assertTrue(result);
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            assertTrue(false);
        }

        // check database content
        CompletableFuture<Boolean> futureResultInitHasVoted = new CompletableFuture<>();
        database.getReference().child("Users").child("testUserId").child("has_voted").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    fail();
                }
                else {
                    futureResultInitHasVoted.complete( (Boolean)task.getResult().getValue() );
                }
            }
        });

        // check database content
        CompletableFuture<String> futureResultRegisteredSection = new CompletableFuture<>();
        database.getReference().child("Users").child("testUserId").child("section").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    fail();
                }
                else {
                    futureResultRegisteredSection.complete( String.valueOf(task.getResult().getValue()) );
                }
            }
        });

        try{
            assertFalse(futureResultInitHasVoted.get());
            assertEquals(Section.IN.toString(), futureResultRegisteredSection.get());
        }catch (Exception e){
            fail();
        }
    }

    @Test
    public void testGetUserSection() {

        database.getReference().child("Users").child("testUserId").child("section").setValue("SC");


        BackendInterface b = new FirebaseBackend();

        try {
            Section result = b.getUserSection("testUserId").get();
            assertEquals(result, Section.SC);
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
        }
    }

    @Ignore
    @Test
    public void testGetUserSectionInexistantPlayer()
    {
        // set database content

        database.getReference().child("Users").child("testUserId").child("section").setValue("SC");


        BackendInterface b = new FirebaseBackend();

        try{
            b.getUserSection("userid sdfadf").get();
        }catch(Throwable e){
            if(e.toString() != "Could not get result from the database"){
                fail();
            }

        }
    }

    @Test
    public void testGetCurrentAttacks() {

        database.getReference().child("Zones").child("testZone").child("owner").setValue("SIE");
        database.getReference().child("Zones").child("testZone").child("IN").setValue(45);
        database.getReference().child("Zones").child("testZone").child("SC").setValue(0);


        BackendInterface b = new FirebaseBackend();

        try {
            Map result = b.getCurrentAttacks("testZone").get();
            assertEquals(result.get(Section.IN), 45);
            assertEquals(result.get(Section.SC), 0);
            assertFalse(result.containsKey("owner"));
        }catch(Exception e){
            Log.e("Error in test", e.toString());
            fail();
        }
    }

    @Test
    public void testGetUserSectionInexistantZone() {
        // set database content

        BackendInterface b = new FirebaseBackend();

        try {
            b.getCurrentAttacks("zoneasfasdfasd").get();
        } catch (Throwable e) {
            if (e.toString() != "Could not get result from the database") {
                fail();
            }

        }
    }
}



