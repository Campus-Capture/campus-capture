package com.github.campus_capture.bootcamp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.authentication.Section;
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

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        Function<Boolean, CompletionStage<Boolean>> register_player_zone_vote = (had_already_voted) -> {

            CompletableFuture<Boolean> futureResultVoteZone = new CompletableFuture<>();

            if(had_already_voted){
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

            if(!zone_vote_registered){
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

        CompletableFuture<List<ScoreItem>> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db =  context.getFirebaseDB();

        DatabaseReference sectionsRef = db.getReference("Sections");

        sectionsRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    List<ScoreItem> scores = new LinkedList<>();
                    task.getResult().getChildren().forEach((section) -> {
                        String sectionName = section.getKey();
                        Long score = (Long)section.child("score").getValue();
                        ScoreItem item = new ScoreItem(sectionName, score.intValue());
                        scores.add(item);

                    });
                    Collections.sort(scores);

                    futureResult.complete(scores);
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<Boolean> initUserInDB(String uid, Section section){
        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+ uid);

        Function<Boolean, CompletionStage<Boolean>> init_has_voted = (set_section_success) -> {

            CompletableFuture<Boolean> futureRegisterUserResult = new CompletableFuture<>();

            if(set_section_success) {
                userRef.child("has_voted").setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                futureRegisterUserResult.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                futureRegisterUserResult.completeExceptionally(new Throwable("Could not init user has_voted"));
                            }
                        });
            }
            else{
                futureRegisterUserResult.complete(false);
            }

            return futureRegisterUserResult;

        };

        return setUserSection(uid, section).thenCompose(init_has_voted);
    }

    @Override
    public CompletableFuture<Boolean> setUserSection(String uid, Section section){

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+ uid);
        CompletableFuture<Boolean> futureSetUserSectionResult = new CompletableFuture<>();
        userRef.child("section").setValue(section.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        futureSetUserSectionResult.complete(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        futureSetUserSectionResult.completeExceptionally(new Throwable("Could not set user section"));
                    }
                });

        return futureSetUserSectionResult;
    }

    @Override
    public CompletableFuture<Section> getUserSection(String uid) {
        CompletableFuture<Section> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+ uid);

        userRef.child("section").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    futureResult.complete( Section.valueOf(String.valueOf(task.getResult().getValue())) );
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<Map<Section, Integer>> getCurrentAttacks(String zoneName) {
        // TODO placeholder until the back-end implementation is done
        Map<Section, Integer> out = new HashMap<>();
        out.put(Section.IN, 2);
        out.put(Section.SC, 1);
        out.put(Section.AR, 0); // This is just to make sure it doesn't get displayed
        return CompletableFuture.completedFuture(out);
    }
}
