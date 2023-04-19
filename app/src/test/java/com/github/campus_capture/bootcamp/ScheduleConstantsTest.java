package com.github.campus_capture.bootcamp;

import static com.github.campus_capture.bootcamp.map.ScheduleConstants.MILLIS_PER_HOUR;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.MILLIS_PER_MIN;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.MILLIS_PER_SEC;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.NO_POSITION_RETRY_DELAY;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.OWNER_REFRESH_DELAY;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.TAKEOVER_DURATION;
import static com.github.campus_capture.bootcamp.map.ScheduleConstants.ZONE_REFRESH_RATE;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScheduleConstantsTest {

    @Test
    public void testConstants()
    {
        Assertions.assertEquals(MILLIS_PER_SEC, 1000);
        Assertions.assertEquals(MILLIS_PER_MIN, 60 * 1000);
        Assertions.assertEquals(MILLIS_PER_HOUR, 60 * 60 * 1000);
        Assertions.assertEquals(ZONE_REFRESH_RATE, 10 * 1000);
        Assertions.assertEquals(OWNER_REFRESH_DELAY, 5 * 1000);
        Assertions.assertEquals(TAKEOVER_DURATION, 15 * 60 * 1000);
        Assertions.assertEquals(NO_POSITION_RETRY_DELAY, 1000);
    }

}
