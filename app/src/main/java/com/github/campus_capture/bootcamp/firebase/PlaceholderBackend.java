package com.github.campus_capture.bootcamp.firebase;

import static com.github.campus_capture.bootcamp.authentication.Section.AR;
import static com.github.campus_capture.bootcamp.authentication.Section.CGC;
import static com.github.campus_capture.bootcamp.authentication.Section.EL;
import static com.github.campus_capture.bootcamp.authentication.Section.GC;
import static com.github.campus_capture.bootcamp.authentication.Section.GM;
import static com.github.campus_capture.bootcamp.authentication.Section.IN;
import static com.github.campus_capture.bootcamp.authentication.Section.MA;
import static com.github.campus_capture.bootcamp.authentication.Section.MT;
import static com.github.campus_capture.bootcamp.authentication.Section.MX;
import static com.github.campus_capture.bootcamp.authentication.Section.NONE;
import static com.github.campus_capture.bootcamp.authentication.Section.PH;
import static com.github.campus_capture.bootcamp.authentication.Section.SC;
import static com.github.campus_capture.bootcamp.authentication.Section.SIE;
import static com.github.campus_capture.bootcamp.authentication.Section.SV;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.github.campus_capture.bootcamp.shop.PowerUp;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlaceholderBackend implements BackendInterface {
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
        out.put("SG1", AR);
        out.put("CO Est", GC);
        out.put("CO Ouest", SIE);
        out.put("BC", IN);
        out.put("INM INR Terasse", SC);
        out.put("INM", CGC);
        out.put("INF INJ", MA);
        out.put("MXC MXD", PH);
        out.put("MXG Terrasse", EL);
        out.put("MXE MXH", SV);
        out.put("ELA ELB", MX);
        out.put("ELL", GM);
        out.put("SV AI", MT);
        out.put("Agora", NONE);
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

    @Override
    public CompletableFuture<Boolean> initUserInDB(String uid, Section section){
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Boolean> setUserSection(String uid, Section section) {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletableFuture<Section> getUserSection(String uid) {
        return CompletableFuture.completedFuture(Section.IN);
    }

    @Override
    public CompletableFuture<Map<Section, Integer>> getCurrentAttacks(String zoneName) {
        Map<Section, Integer> out = new HashMap<>();
        out.put(Section.IN, 2);
        out.put(Section.SC, 1);
        out.put(Section.AR, 0); // This is just to make sure it doesn't get displayed
        return CompletableFuture.completedFuture(out);
    }

    @Override
    public CompletableFuture<List<PowerUp>> getPowerUps() {
        return CompletableFuture.completedFuture(
                Collections.singletonList(new PowerUp("PowerUpTest", 70, 80))
        );
    }

    @Override
    public CompletableFuture<Integer> getMoney() {
        return null;
    }
}
