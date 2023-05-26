package com.github.campus_capture.bootcamp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.github.campus_capture.bootcamp.shop.PowerUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
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
                    if(task.getResult().getValue() == null){
                        futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                    } else{
                        futureResult.complete((Boolean) task.getResult().getValue());
                    }
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
                        });

                userRef.child("money").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                futureRegisterUserResult.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                futureRegisterUserResult.completeExceptionally(new Throwable("Could not init user money"));
                            }
                        });

                userRef.child("money").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                futureRegisterUserResult.complete(true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                futureRegisterUserResult.completeExceptionally(new Throwable("Could not init user money"));
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
                    String result = String.valueOf(task.getResult().getValue());
                    if(result == "null"){
                        futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                    } else{
                        futureResult.complete( Section.valueOf(result) );
                    }
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<Map<Section, Integer>> getCurrentAttacks(String zoneName) {
        CompletableFuture<Map<Section, Integer>> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference zoneRef = db.getReference("Zones/" + zoneName);

        zoneRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    Map<Section, Integer> result = new HashMap<>();
                    for(DataSnapshot child : task.getResult().getChildren()){
                        try {
                            result.put(Section.valueOf(child.getKey()), ((Long)child.getValue()).intValue() );
                        } catch (IllegalArgumentException e){
                            // expected exception for the "owner" child which is not a section
                            // TODO maybe cleaner to add a condition instead of a try catch ?
                        }
                    }
                    futureResult.complete( result );
                }
            }
        });

        return futureResult;
    }

    @Override
    public CompletableFuture<List<PowerUp>> getPowerUps() {
        CompletableFuture<List<PowerUp>> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("PowerUp");

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    List<PowerUp> powerUpList = new ArrayList<>();

                    task.getResult().getChildren().forEach((powerUp) -> {
                        String powerUpName = powerUp.getKey();

                        Integer value = powerUp.child("value").getValue(int.class);
                        Integer fund = powerUp.child("funds/" + User.getSection()).getValue(int.class);
                        if(value==null){
                            Log.e("FirebaseBackend", "Value is null.");
                        } else if(fund==null) {
                            Log.e("FirebaseBackend", "Fund is null. Section: " + User.getSection());
                        } else {
                            powerUpList.add(new PowerUp(powerUpName, fund, value));
                        }

                    });
                    futureResult.complete(powerUpList);
                }
            }
        });
        return futureResult;
    }

    @Override
    public CompletableFuture<Integer> getMoney() {
        CompletableFuture<Integer> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        DatabaseReference userRef = db.getReference("Users/"+User.getUid()+"/money");

        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
                else {
                    futureResult.complete(task.getResult().getValue(Integer.class));
                }
            }
        });
        return futureResult;
    }

    @Override
    public CompletableFuture<Boolean> sendMoney(String name, int money) {


        CompletableFuture<Boolean> result = new CompletableFuture<>();

        if(money <= 0){
            result.completeExceptionally(new Throwable("Can't send money <= 0"));
            return result;
        }

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();
        DatabaseReference userMoneyRef = db.getReference("Users/"+User.getUid()+"/money");
        DatabaseReference powerupMoneyRef = db.getReference("PowerUp/"+name+"/funds/"+User.getSection());

        // Spend the money first, if something goes wrong, as such no new funds are created
        userMoneyRef.setValue(ServerValue.increment(-money))
                .addOnSuccessListener(
                    unused ->
                        powerupMoneyRef.setValue(ServerValue.increment(money))
                            .addOnSuccessListener(unused1 -> result.complete(true))
                            .addOnFailureListener(unused2 -> result.completeExceptionally(new Throwable("Failed to add the money to the powerup"))))
                .addOnFailureListener(
                    unused3 ->
                        result.completeExceptionally(new Throwable("Failed to take the money out of the player's funds")));

        return result;

    }

    @Override
    public CompletableFuture<Boolean> isUserInDB(String uid) {
        CompletableFuture<Boolean> futureResult = new CompletableFuture<>();

        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();
        DatabaseReference userRef = db.getReference("Users");
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    futureResult.complete(task.getResult().hasChild(uid));
                } else {
                    futureResult.completeExceptionally(new Throwable("Could not get result from the database"));
                }
            }
        });

        return futureResult;
    }


}
