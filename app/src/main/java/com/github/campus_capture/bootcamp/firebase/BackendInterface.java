package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.github.campus_capture.bootcamp.shop.PowerUp;

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
    CompletableFuture<Boolean> attackZone(String uid, Section s, String zonename);

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

    /**
     * Method to get the current attacks in a given zone, ordered by section
     * @param zoneName the name of the zone
     * @return map from section to attack number
     */
    CompletableFuture<Map<Section, Integer>> getCurrentAttacks(String zoneName);

    /**
     * Method to get the current available powerUps
     * @return map of the all power ups
     */
    CompletableFuture<List<PowerUp>> getPowerUps();

    /**
     * Method to get the money of the user
     * @return The money of the user
     */
    CompletableFuture<Integer> getMoney();

    /**
     * Method to signal to the back-end that a player has given some money for a given powerup
     * This wraps the two previous unsafe methods from #149 into one, but pushes the logic for
     * determining the success of transmission and so on behind the interface
     * @param name The name of the powerup
     * @param money the amount of funds transferred
     * @return boolean: success
     */
    CompletableFuture<Boolean> sendMoney(String name, int money);

    /**
     * Method to retrieve the section's votes on the zones (not attacks but planning)
     * @param s the section
     * @return a list of strings of the names of the zones
     */
    CompletableFuture<List<String>> getSectionVotes(Section s);

    /**
     * Method for a player to vote for a zone for their team planning
     * @param name
     * @return
     */
    CompletableFuture<Boolean> voteZone(String name);
}
