package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;

import android.view.View;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class ScoreboardFragmentTest {

    final BackendInterface mock = new PlaceholderBackend() {
        @Override
        public CompletableFuture<Boolean> attackZone(String uid, Section s, String zonename) {
            return CompletableFuture.completedFuture(false);
        }

        @Override
        public CompletableFuture<Boolean> hasAttacked(String uid) {
            return CompletableFuture.completedFuture(false);
        }

        @Override
        public CompletableFuture<Map<String, Section>> getCurrentZoneOwners() {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletableFuture<List<ScoreItem>> getScores() {
            return CompletableFuture.completedFuture(
                    Arrays.asList(new ScoreItem("IC", 1000), new ScoreItem("SC", 999))
            );
        }
    };

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkThatScoreboardDisplaysCorrectValues()
    {
        MainActivity.backendInterface = mock;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_scoreboard)).perform(ViewActions.click());

        onView(withIndex(ViewMatchers.withId(R.id.item_number), 1)).check(matches(withText(containsString("SC"))));
        onView(withIndex(ViewMatchers.withId(R.id.content), 1)).check(matches(withText(containsString("999"))));

        Intents.release();
    }

    /**
     * Helper method "inspired" by this <a href="https://stackoverflow.com/questions/29378552/in-espresso-how-to-avoid-ambiguousviewmatcherexception-when-multiple-views-matc">SO post</a>
     * @param matcher the matcher
     * @param index the index to match
     * @return the viewmatcher
     */
    private static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }

}
