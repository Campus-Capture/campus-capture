package com.github.campus_capture.bootcamp;

import androidx.test.core.app.ApplicationProvider;

import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.PlaceholderFirebaseInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class PlaceholderBackendInterfaceTest {

    @Test
    public void testVoteZoneReturnsCorrect()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();

        try {
            assertTrue(t.voteZone("", Section.IN, "").get());
        }catch(Exception e){
            fail();
        }
    }

    @Test
    public void testCurrentZoneOwners()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();

        try{
            Map<String, Section> owners = t.getCurrentZoneOwners().get();
            assertEquals(owners.get("campus"), Section.IN);
        }catch(Exception e){
            fail();
        }
    }

    @Test
    public void testScoresAreWellOrdered()
    {
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();

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
        PlaceholderFirebaseInterface t = new PlaceholderFirebaseInterface();

        try{
            assertFalse(t.hasAttacked("kek").get());
        }catch(Exception e){
            fail();
        }

    }

}
