package com.github.campus_capture.bootcamp.activities;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.adevinta.android.barista.interaction.BaristaSleepInteractions.sleep;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.github.campus_capture.bootcamp.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void navigationTest() {

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Revenir en haut de la page"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction navigationMenuItemView = onView(
                allOf(withId(R.id.nav_greeting),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.nav_view),
                                                0)),
                                2),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Revenir en haut de la page"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction navigationMenuItemView2 = onView(
                allOf(withId(R.id.nav_test),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.nav_view),
                                                0)),
                                4),
                        isDisplayed()));
        navigationMenuItemView2.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withContentDescription("Revenir en haut de la page"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

        ViewInteraction navigationMenuItemView3 = onView(
                allOf(withId(R.id.nav_profile),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.nav_view),
                                                0)),
                                5),
                        isDisplayed()));
        navigationMenuItemView3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withContentDescription("Revenir en haut de la page"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton4.perform(click());

        ViewInteraction navigationMenuItemView5 = onView(
                allOf(withId(R.id.nav_scoreboard),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.nav_view),
                                                0)),
                                7),
                        isDisplayed()));
        navigationMenuItemView5.perform(click());

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withContentDescription("Revenir en haut de la page"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.app_bar_navigation_drawer),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton5.perform(click());

        ViewInteraction navigationMenuItemView4 = onView(
                allOf(withId(R.id.nav_rules),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.design_navigation_view),
                                        childAtPosition(
                                                withId(R.id.nav_view),
                                                0)),
                                6),
                        isDisplayed()));
        navigationMenuItemView4.perform(click());

        sleep(5000);
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
