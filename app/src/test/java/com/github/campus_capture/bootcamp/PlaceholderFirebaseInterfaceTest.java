package com.github.campus_capture.bootcamp;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.PlaceholderFirebaseInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class PlaceholderFirebaseInterfaceTest {

    @Test
    public void testVoteZoneReturnsCorrect()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();
        assertTrue(t.voteZone("", Section.IN, ""));
    }

    @Test
    public void testCurrentZoneOwners()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();
        Map<String, Section> owners = t.getCurrentZoneOwners();
        assertEquals(owners.get("campus"), Section.IN);
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

    @Test
    public void testIfPlayerAlreadyAttacked()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();
        assertFalse(t.hasAttacked("kek"));
    }

}
