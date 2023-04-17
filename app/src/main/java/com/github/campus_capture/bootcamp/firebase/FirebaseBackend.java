package com.github.campus_capture.bootcamp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class FirebaseBackend implements BackendInterface{
    @Override
    public CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename) {

        AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+ uid);

        Function<Boolean, CompletionStage<Boolean>> register_player_has_voted = (has_voted) -> {
            //register that player has voted
            CompletableFuture<Boolean> futureResultUser = new CompletableFuture<>();

            if(has_voted == true){
                futureResultUser.complete(false);
            } else {
                userRef.child("has_voted").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Log.d("MY_TAG", "has voted set Value success");
                                futureResultUser.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MY_TAG", "has voted set Value failure");
                                futureResultUser.completeExceptionally(new Throwable("Could not register that user did vote"));
                            }
                        });
            }
            return futureResultUser;
        };

        DatabaseReference zonesRef = db.getReference("Zones");
        CompletableFuture<Boolean> futureResultZone = new CompletableFuture<>();

        Function<Boolean, CompletionStage<Boolean>> register_player_vote = (has_voted) -> {

            if(has_voted == true){
                futureResultZone.complete(false);
            } else {
                zonesRef.child(zonename).child(s.toString()).setValue(ServerValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("MY_TAG", "register player vote set Value success");
                                futureResultZone.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("MY_TAG", "register player vote set Value failure");
                                futureResultZone.completeExceptionally(new Throwable("Could not register the user vote"));
                            }
                        });
            }
            return futureResultZone;
        };

        CompletableFuture<Boolean> futureResultHasVoted = new CompletableFuture<>();
        userRef.child("has_voted").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("MY_TAG", "read has voted failure");
                    futureResultHasVoted.completeExceptionally(new Throwable("Could not read if user has voted"));
                }
                else {
                    Log.d("MY_TAG", "read has voted success " + (Boolean)task.getResult().getValue());
                    futureResultHasVoted.complete( (Boolean)task.getResult().getValue() );
                }
            }
        });


        futureResultHasVoted.thenCompose(register_player_vote).thenCompose(register_player_has_voted);

        return futureResultHasVoted;
    }

    @Override
    public CompletableFuture<Boolean> hasAttacked(String uid) {

        CompletableFuture<Boolean> futureResult = new CompletableFuture<>();

        AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+ uid);

        userRef.child("has_voted").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    futureResult.complete((Boolean) task.getResult().getValue());
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<Map<String, Section>> getCurrentZoneOwners() {
        CompletableFuture<Map<String, Section>> futureResult = new CompletableFuture<>();

        AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference zonesRef = db.getReference("Zones");

        zonesRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    Map<String, Section> result = new HashMap<>();
                    task.getResult().getChildren().forEach((zone) -> {
                        String zoneName = zone.getKey();
                        Section owner = Section.valueOf(String.valueOf(zone.child("owner").getValue()));
                        result.put(zoneName, owner);
                    });
                    futureResult.complete(result);
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<List<ScoreItem>> getScores() {
        CompletableFuture<List<ScoreItem>> futureResult = new CompletableFuture<>();

        AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference zonesRef = db.getReference("Sections");

        zonesRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    List<ScoreItem> scores = new LinkedList<>();
                    task.getResult().getChildren().forEach((section) -> {
                        String sectionName = section.getKey();
                        Integer score = (Integer)section.child("score").getValue();
                        ScoreItem item = new ScoreItem(sectionName, score);
                        scores.add(item);
                    });
                    Collections.sort(scores);
                    futureResult.complete(scores);
                }
            }
        });

        return futureResult;
    }
}
