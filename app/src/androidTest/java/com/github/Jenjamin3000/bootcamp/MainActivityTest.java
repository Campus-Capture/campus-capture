package com.github.Jenjamin3000.bootcamp;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import  static androidx.test.espresso.Espresso.onView;

import android.inputmethodservice.Keyboard;

import java.util.regex.Matcher;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    /*@Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void GoToGreetingActivityTest(){
        Intents.init();
        onView(ViewMatchers.withId(R.id.mainUserNameText)).perform(ViewActions.typeText("Roger"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.mainGoButton)).perform(ViewActions.click());

        Intents.intended(IntentMatchers.hasComponent(GreetingActivity.class.getName()));
        Intents.release();

    }

    @Test
    public void GoodGreetingMessageTest(){
        onView(ViewMatchers.withId(R.id.mainUserNameText)).perform(ViewActions.typeText("Roger"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.mainGoButton)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.greetingText)).check(ViewAssertions.matches(ViewMatchers.withText("Bonjour Roger!")));
    }*/
}
