package com.github.Jenjamin3000.bootcamp.scoreboard;


import java.util.List;

/**
 * Score handler interface, allowing us to retrieve the scores from the database to be displayed
 * in the app
 */
public interface ScoreHandler {

    /**
     * Getter to retrieve the scores from the database
     * @return A sorted list of ScoreItems, descending in points
     */
    List<ScoreItem> getScores();

}
