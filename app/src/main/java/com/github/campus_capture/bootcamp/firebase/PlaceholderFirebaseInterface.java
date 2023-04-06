package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlaceholderFirebaseInterface implements BackendInterface {
    @Override
    public CompletableFuture<Boolean> voteZone(String uid, Section s, String zonename) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> hasAttacked(String uid) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Map<String, Section>> getCurrentZoneOwners() {
        Map<String, Section> out = new HashMap<>();
        out.put("campus", Section.IN);
        return CompletableFuture.completedFuture(out);
    }

    @Override
    public CompletableFuture<List<ScoreItem>> getScores() {
        List<ScoreItem> scores = Arrays.asList(
                new ScoreItem("IN", 1000),
                new ScoreItem("SC", 999),
                new ScoreItem("SV", 0),
                new ScoreItem("AR", -10000),
                new ScoreItem("UNIL", -10000000)
        );

        Collections.sort(scores);

        return CompletableFuture.completedFuture(scores);
    }
}
