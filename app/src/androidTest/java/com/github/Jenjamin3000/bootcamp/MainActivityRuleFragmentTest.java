package com.github.Jenjamin3000.bootcamp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityRuleFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void RulesFragmentShowTheRightContent() {
        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_rules)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.rules_text)).check(matches(withText(containsString("The rules stocked in firebase..."))));

        Intents.release();
    }
}
