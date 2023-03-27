package com.github.campus_capture.bootcamp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.PlaceholderFirebaseInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.junit.Test;

import java.util.List;

public class PlaceholderFirebaseInterfaceTest {

    @Test
    public void testVoteZoneReturnsCorrect()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();
        assertFalse(t.voteZone("", Section.IN, ""));
    }

    @Test
    public void testCurrentZoneOwners()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();
        assertNull(t.getCurrentZoneOwners());
    }

    @Test
    public void testScoresAreWellOrdered()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();
        List<ScoreItem> scores = t.getScores();

        for(int i = 0; i < scores.size() - 1; i++)
        {
            assertTrue(scores.get(i).getValue() >= scores.get(i + 1).getValue());
        }
    }

}
