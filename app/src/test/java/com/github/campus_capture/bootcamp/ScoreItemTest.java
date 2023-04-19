package com.github.campus_capture.bootcamp;

import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScoreItemTest {

    @Test
    public void scoreItemGettersWorkCorrectly()
    {
        ScoreItem item1 = new ScoreItem("IN", 10);
        Assertions.assertEquals(item1.getLabel(), "IN");
        Assertions.assertEquals(item1.getValue(), 10);

        ScoreItem item2 = new ScoreItem("SC", 9);
        Assertions.assertEquals(item2.getLabel(), "SC");
        Assertions.assertEquals(item2.getValue(), 9);
    }

    @Test
    public void scoreItemComparesCorrectly()
    {
        ScoreItem item1 = new ScoreItem("IN", 10);
        ScoreItem item2 = new ScoreItem("SC", 9);
        ScoreItem item3 = new ScoreItem("AR", 9);
        ScoreItem item4 = new ScoreItem("AR", 9);

        Assertions.assertEquals(item1.compareTo(item2), -1);
        Assertions.assertEquals(item1.compareTo(item3), -1);
        Assertions.assertEquals(item2.compareTo(item1), 1);
        Assertions.assertEquals(item3.compareTo(item1), 1);
        Assertions.assertEquals(item2.compareTo(item3), 18);
        Assertions.assertEquals(item3.compareTo(item2), -18);
        Assertions.assertEquals(item3.compareTo(item4), 0);
    }
}
