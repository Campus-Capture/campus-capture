package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import java.util.List;
import java.util.Map;

/**
 * Interface modeling all of the interactions with Firebase
 */
public interface FirebaseInterface {

    /**
     * Method to signal to Firebase that the user has voted for a zone
     * @param uid the player's UID
     * @param s the section the user is in
     * @param zonename the name of the zone
     * @return boolean: success
     */
    boolean voteZone(String uid, Section s, String zonename);

    /**
     * Method to retrieve a map of the sections and their owners
     * @return Map<String, Section>: Section may be null if there is no owner
     */
    Map<String, Section> getCurrentZoneOwners();

    /**
     * Method to retrieve the score of the different sections
     * @return an ordered list of score items
     */
    List<ScoreItem> getScores();
}
