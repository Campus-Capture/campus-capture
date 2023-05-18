package com.github.campus_capture.bootcamp;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Map;

public class PlaceholderBackendInterfaceTest {

    @Test
    public void testVoteZoneReturnsCorrect()
    {
        PlaceholderBackend t = new PlaceholderBackend();

        try {
            assertTrue(t.voteZone("", Section.IN, "").get());
        }catch(Exception e){
            fail();
        }
    }

    @Test
    public void testCurrentZoneOwners()
    {
        PlaceholderBackend t = new PlaceholderBackend();

        try{
            Map<String, Section> owners = t.getCurrentZoneOwners().get();
            assertEquals(owners.get("CE"), Section.PH);
        }catch(Exception e){
            fail();
        }
    }

    @Test
    public void testScoresAreWellOrdered()
    {
        PlaceholderBackend t = new PlaceholderBackend();

        try{
            List<ScoreItem> scores = t.getScores().get();

            for(int i = 0; i < scores.size() - 1; i++)
            {
                assertTrue(scores.get(i).getValue() >= scores.get(i + 1).getValue());
            }
        }catch(Exception e){
            fail();
        }

    }

    @Test
    public void testIfPlayerAlreadyAttacked()
    {
        PlaceholderBackend t = new PlaceholderBackend();

        try{
            assertFalse(t.hasAttacked("kek").get());
        }catch(Exception e){
            fail();
        }

    }

}
