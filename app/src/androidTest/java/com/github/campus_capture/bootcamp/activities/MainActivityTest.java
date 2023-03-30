package com.github.campus_capture.bootcamp.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.adevinta.android.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep;

import static org.hamcrest.Matchers.allOf;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    public static final String DRAWER_BUTTON = "Revenir en haut de la page";

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void NavigationDrawerOpensWhenMenuButtonPressed()
    {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        assertDisplayed(R.id.nav_view);
    }

    @Test
    public void NavigationDrawerClosesWhenActionIsExecuted()
    {
        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        clickOn(R.id.nav_main);

        assertNotDisplayed(R.id.nav_view);
    }

    @Ignore("Buggy in CI")
    @Test
    public void NavigationDrawerClosesWhenSwiped() {
        Intents.init();
        onView(ViewMatchers.withContentDescription(DRAWER_BUTTON))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_view))
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(R.id.nav_view)).perform(ViewActions.swipeLeft());

        onView(ViewMatchers.withId(R.id.nav_view))
                .check(matches(Matchers.not(ViewMatchers.isDisplayed())));

        Intents.release();
    }

    @Test
    public void navigationTest() {

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        clickOn(R.id.nav_greeting);

        assertDisplayed(R.id.nav_greeting);

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        clickOn(R.id.nav_maps);

        assertDisplayed(R.id.nav_maps);

        ViewInteraction appCompatImageButton20 = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton20.perform(click());

        clickOn(R.id.nav_profile);

        assertDisplayed(R.id.nav_profile);

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

       clickOn(R.id.nav_scoreboard);

        assertDisplayed(R.id.nav_scoreboard);

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withContentDescription(DRAWER_BUTTON),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton5.perform(click());

        clickOn(R.id.nav_rules);

        assertDisplayed(R.id.nav_rules);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
