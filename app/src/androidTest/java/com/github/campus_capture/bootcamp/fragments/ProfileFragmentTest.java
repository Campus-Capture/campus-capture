package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertNotNull;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.User;

import org.junit.Rule;
import org.junit.Test;

public class ProfileFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void checkInviteButtonWorks()
    {
        // Note: this test is a bit hard to do since there's no check if the menu correctly opens
        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_profile)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.invite_button)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.invite_button)).check(matches(withText("Invite your friends")));

        onView(ViewMatchers.withId(R.id.invite_button)).perform(ViewActions.click());

        Intents.release();
    }

    @Test
    public void TestUidNameExists() {
        assertNotNull(User.getUid());
    }
}
