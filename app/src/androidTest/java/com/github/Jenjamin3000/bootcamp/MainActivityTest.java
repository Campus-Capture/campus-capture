package com.github.Jenjamin3000.bootcamp;

import static androidx.test.espresso.Espresso.onView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    private final static String email = "roger@federer.com";
    private final static String phone = "0791111111";

    @Test
    public void SetAndGet(){
        onView(ViewMatchers.withId(R.id.GetSetEmail)).perform(ViewActions.typeText(email));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.GetSetPhone)).perform(ViewActions.typeText(phone));
        Espresso.closeSoftKeyboard();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        onView(ViewMatchers.withId(R.id.SetButton)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.GetButton)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.GetSetAnswer)).check(ViewAssertions.matches(ViewMatchers.withText(email)));


    }
    /*@Test
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
