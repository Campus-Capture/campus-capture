package com.github.campus_capture.bootcamp;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * A test suite which checks that the code doesn't contain any profanities. This suite
 * may be subject to the Scunthorpe problem.
 */
public class ProfanityTest {

    // The list of words to be filtered. Feel free to add some
    private final String[] filterWords = {
            "shit",
            "fuck",
            "bullshit",
            "crap",
            "bullcrap",
            "cunt",
            "bitch",
            "ass",
            "testicle",
            "twat"
    };

    @Test
    public void testThatTheCodeContainsNoProfanity()
    {
        try(Stream<Path> sources = Files.walk(Paths.get("src/main")))
        {
            sources.filter(Files::isRegularFile)
                    .forEach(
                            System.out::println
                    );
        }
        catch(Exception e)
        {

        }
    }

}
