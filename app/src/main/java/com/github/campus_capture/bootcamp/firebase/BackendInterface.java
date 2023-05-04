package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface modeling all of the interactions with Firebase
 */
public interface BackendInterface {

    /**
     * Method to signal to Firebase that the user has voted for a zone
     * @param uid the player's UID
     * @param s the section the user is in
     * @param zonename the name of the zone
     * @return boolean: success
     */
    CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename);

    /**
     * Method to check if a player has attacked during the current take-over or not
     * @param uid the player's UID
     */
    CompletableFuture<Boolean> hasAttacked(String uid);

    /**
     * Method to retrieve a map of the sections and their owners
     * @return Map<String, Section>: Section may be null if there is no owner
     */
    CompletableFuture<Map<String, Section>> getCurrentZoneOwners();

    /**
     * Method to retrieve the score of the different sections
     * @return an ordered list of score items
     */
    CompletableFuture<List<ScoreItem>> getScores();

    /**
     * Register the user with all initialized attributes in the DB
     * @return boolean: success
     */
    CompletableFuture<Boolean> initUserInDB(String uid, Section section);

    CompletableFuture<Boolean> setUserSection(String uid, Section section);

    CompletableFuture<Section> getUserSection(String uid);
}
