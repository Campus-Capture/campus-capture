package com.github.campus_capture.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.activities.MainActivity;

import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void NavigationDrawerOpensWhenMenuButtonPressed()
    {
        Intents.init();
        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_view))
                .check(matches(ViewMatchers.isDisplayed()));

        Intents.release();
    }

    @Test
    public void NavigationDrawerClosesWhenActionIsExecuted()
    {
        Intents.init();
        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_main)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_view))
                .check(matches(Matchers.not(ViewMatchers.isDisplayed())));

        Intents.release();
    }

    @Ignore("Buggy in CI")
    @Test
    public void NavigationDrawerClosesWhenSwiped() {
        Intents.init();
        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_view))
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.nav_view)).perform(ViewActions.swipeLeft());

        onView(ViewMatchers.withId(R.id.nav_view))
                .check(matches(Matchers.not(ViewMatchers.isDisplayed())));

        Intents.release();
    }

    @Test
    public void MainActivityFragmentContainerIsUpdatedWhenActionIsExecuted() {
        Intents.init();
        onView(ViewMatchers.withId(R.id.textMain))
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_greeting)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.textGreeting))
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_main)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.textMain))
                .check(matches(ViewMatchers.isDisplayed()));

        Intents.release();
    }
}
