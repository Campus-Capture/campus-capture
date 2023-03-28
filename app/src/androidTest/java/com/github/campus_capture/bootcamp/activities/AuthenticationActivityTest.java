package com.github.campus_capture.bootcamp.activities;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
public class AuthenticationActivityTest {

    @Rule
    public ActivityScenarioRule<AuthenticationActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(AuthenticationActivity.class);

    @Test
    public void authenticationActivityTest2() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.login_confirm_button), withText("Log in"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(com.firebase.ui.auth.R.id.email_button), withText("Se connecter avec une adresse e-mail"),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.btn_holder),
                                        childAtPosition(
                                                withId(com.firebase.ui.auth.R.id.container),
                                                0)),
                                0)));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(com.firebase.ui.auth.R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.firebase.ui.auth.R.id.email_layout),
                                        0),
                                0)));
        textInputEditText.perform(scrollTo(), replaceText("david.karoubi@epfl.ch"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(com.firebase.ui.auth.R.id.button_next), withText("Suivant"),
                        childAtPosition(
                                allOf(withId(com.firebase.ui.auth.R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(com.firebase.ui.auth.R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.firebase.ui.auth.R.id.password_layout),
                                        0),
                                0)));
        textInputEditText2.perform(scrollTo(), replaceText("password"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(com.firebase.ui.auth.R.id.button_done), withText("Se connecter"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatButton3.perform(scrollTo(), click());
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
