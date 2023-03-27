package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlaceholderFirebaseInterface implements FirebaseInterface {
    @Override
    public boolean voteZone(String uid, Section s, String zonename) {
        return false;
    }

    @Override
    public Map<String, Section> getCurrentZoneOwners() {
        return null;
    }

    @Override
    public List<ScoreItem> getScores() {
        List<ScoreItem> scores = Arrays.asList(
                new ScoreItem("IN", 1000),
                new ScoreItem("SC", 999),
                new ScoreItem("SV", 0),
                new ScoreItem("AR", -10000),
                new ScoreItem("UNIL", -10000000)
        );

        Collections.sort(scores);

        return scores;
    }
}