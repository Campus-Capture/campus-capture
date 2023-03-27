package com.github.campus_capture.bootcamp;

import static com.github.campus_capture.bootcamp.map.ScheduleConstants.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScheduleConstantsTest {

    @Test
    public void testConstants()
    {
        assertEquals(MILLIS_PER_SEC, 1000);
        assertEquals(MILLIS_PER_MIN, 60 * 1000);
        assertEquals(MILLIS_PER_HOUR, 60 * 60 * 1000);
        assertEquals(ZONE_REFRESH_RATE, 10 * 1000);
        assertEquals(OWNER_REFRESH_DELAY, 5 * 1000);
        assertEquals(TAKEOVER_DURATION, 15 * 60 * 1000);
        assertEquals(NO_POSITION_RETRY_DELAY, 1000);
    }

}
