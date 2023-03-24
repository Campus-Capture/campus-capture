package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.Section;

/**
 * Interface modeling all of the interactions with Firebase
 */
public interface FirebaseInterface {

    // TODO should this be the zone name or the uid?
    /**
     * Method to signal to Firebase that the user has voted for a zone
     * @param s the section the user is in
     * @param zonename the name of the zone
     * @return boolean: success
     */
    public boolean voteZone(Section s, String zonename);

}
