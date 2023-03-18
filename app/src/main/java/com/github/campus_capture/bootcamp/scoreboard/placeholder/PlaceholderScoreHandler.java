package com.github.campus_capture.bootcamp.scoreboard.placeholder;

import com.github.campus_capture.bootcamp.scoreboard.ScoreHandler;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Placeholder class to supply some scores
 */
public class PlaceholderScoreHandler implements ScoreHandler {

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
