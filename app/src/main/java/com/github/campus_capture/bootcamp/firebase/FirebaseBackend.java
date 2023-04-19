package com.github.campus_capture.bootcamp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FirebaseBackend implements BackendInterface{
    @Override
    public CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename) {

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        Function<Boolean, CompletionStage<Boolean>> register_player_zone_vote = (had_already_voted) -> {

            CompletableFuture<Boolean> futureResultVoteZone = new CompletableFuture<>();

            if(had_already_voted == true){
                futureResultVoteZone.complete(false);
            } else {
                DatabaseReference zonesRef = db.getReference("Zones");
                zonesRef.child(zonename).child(s.toString()).setValue(ServerValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                futureResultVoteZone.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                futureResultVoteZone.completeExceptionally(new Throwable("Could not register the user vote"));
                            }
                        });
            }
            return futureResultVoteZone;
        };

        Function<Boolean, CompletionStage<Boolean>> register_player_has_voted = (zone_vote_registered) -> {
            //register that player has voted
            CompletableFuture<Boolean> futureResultUser = new CompletableFuture<>();

            if(zone_vote_registered == false){
                futureResultUser.complete(false);
            } else {
                DatabaseReference userRef = db.getReference("Users/"+ uid);
                userRef.child("has_voted").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                futureResultUser.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                futureResultUser.completeExceptionally(new Throwable("Could not register that user did vote"));
                            }
                        });
            }
            return futureResultUser;
        };

        return hasAttacked(uid).thenCompose(register_player_zone_vote).thenCompose(register_player_has_voted);
    }

    @Override
    public CompletableFuture<Boolean> hasAttacked(String uid) {

        CompletableFuture<Boolean> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
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

        AppContext context = AppContext.getAppContext();
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
    public CompletableFuture<List<ScoreItem>> getScores(){

        //TODO remove
        Log.d("MY_TAG", "get scores called");

        CompletableFuture<List<ScoreItem>> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db =  context.getFirebaseDB();

        DatabaseReference sectionsRef = db.getReference("Sections");

        sectionsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                //TODO remove
                Log.d("MY_TAG", "on complete called");

                if (!task.isSuccessful()) {
                    //TODO remove
                    Log.d("MY_TAG", "failure");

                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    //TODO remove
                    Log.d("MY_TAG", "success");

                    List<ScoreItem> scores = new LinkedList<>();
                    task.getResult().getChildren().forEach((section) -> {
                        String sectionName = section.getKey();
                        Long score = (Long)section.child("score").getValue();
                        ScoreItem item = new ScoreItem(sectionName, score.intValue());
                        scores.add(item);

                    });
                    Collections.sort(scores);

                    //TODO remove
                    Log.d("MY_TAG", "sorted");
                    futureResult.complete(scores);
                }
            }
        });

        //TODO remove
        Log.d("MY_TAG", "return future");

        return futureResult;
    }
}
