package com.github.campus_capture.bootcamp.firebase;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FirebaseBackend implements BackendInterface{
    @Override
    public CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename) {

        AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
        FirebaseDatabase db = context.getFirebaseDB();

        //register that player uid voted
        CompletableFuture<Boolean> futureResult1 = new CompletableFuture<>();

        DatabaseReference userRef = db.getReference("Users/"+ User.getUid());

        userRef.child("did_vote").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        futureResult1.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        futureResult1.completeExceptionally(new Throwable("Could not register that user did vote"));
                    }
                });;;


        //register the vote
        CompletableFuture<Boolean> futureResult2 = new CompletableFuture<>();

        DatabaseReference zonesRef = db.getReference("Zones");

        zonesRef.child(zonename).child(s.toString()).setValue(ServerValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        futureResult2.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        futureResult2.completeExceptionally(new Throwable("Could not register user vote"));
                    }
                });;

        return futureResult1.thenCombine(futureResult2, (result1, result2) -> (result1 && result2));
    }

    @Override
    public CompletableFuture<Boolean> hasAttacked(String uid) {

        CompletableFuture<Boolean> futureResult = new CompletableFuture<>();

        AppContext context = (AppContext) ApplicationProvider.getApplicationContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+ User.getUid());

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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
                    List<ScoreItem> result = new LinkedList<>();
                    task.getResult().getChildren().forEach((section) -> {
                        String sectionName = section.getKey();
                        Integer score = (Integer)section.child("score").getValue();
                        ScoreItem item = new ScoreItem(sectionName, score);
                        result.add(item);
                    });
                    futureResult.complete(result);
                }
            }
        });

        return futureResult;
    }
}