package com.github.campus_capture.bootcamp.scoreboard;

/**
 * A class representing an item in the score board
 */
public class ScoreItem implements Comparable<ScoreItem> {
    private final String label; // The label (team name)
    private final int value; // The value (team score)

    /**
     * Item constructor
     * @param label Label (team name)
     * @param value Value (team score)
     */
    public ScoreItem(String label, int value)
    {
        this.label = label;
        this.value = value;
    }

    /**
     * Label (team name) getter
     * @return String containing the team name
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Value (team score) getter
     * @return int
     */
    public int getValue()
    {
        return value;
    }

    /**
     * Override of the method: compare the scores first, and then compare the names of the teams
     * if the scores are identical
     * @param i the object to be compared.
     * @return comparison int
     */
    @Override
    public int compareTo(ScoreItem i) {
        int res = -Integer.compare(value, i.getValue());
        return (res == 0) ? label.compareTo(i.getLabel()) : res;
    }
}
