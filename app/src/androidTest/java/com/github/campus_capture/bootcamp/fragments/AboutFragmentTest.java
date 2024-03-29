package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AboutFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> testAbout = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkThatAboutIsCorrectlyDisplayed() throws InterruptedException {
        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        Thread.sleep(1000);

        onView(ViewMatchers.withId(R.id.nav_about)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.about_text_view)).check(matches(withText(containsString("This app was made"))));

        Intents.release();
    }

}
