package com.github.campus_capture.bootcamp.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.PlaceholderBackend;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class VotePowerUpFragmentTest {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * Setup the emulator and ensures that no user is already signed in in the app.
     */
    @BeforeClass
    public static void setup() {
        MainActivity.backendInterface = new PlaceholderBackend();

        User.setSection(Section.IN);
    }

    @Test
    public void shopIsCorrectlySetup() {


        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_power_up)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.power_up_fund)).check(matches(withText("Teams fund: 70")));
        onView(ViewMatchers.withId(R.id.power_up_value)).check(matches(withText("Value: 80")));
        onView(ViewMatchers.withId(R.id.power_up_name)).check(matches(withText("PowerUpTest")));
    }

    @Test
    public void scrollBarIsScrolling() {
        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_power_up)).perform(ViewActions.click());

        //Check that initially, the text shows nothing
        onView(ViewMatchers.withId(R.id.power_up_spend_text)).check(matches(withText("")));

        //Check that if we click on the middle of the bar, we choose 40
        onView(ViewMatchers.withId(R.id.power_up_seek_bar)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.power_up_spend_text)).check(matches(withText("I spend 25 coins for my team.")));


        //Check that if we swipe on the bar, we choose 80
        onView(ViewMatchers.withId(R.id.power_up_seek_bar)).perform(ViewActions.swipeRight());
        onView(ViewMatchers.withId(R.id.power_up_spend_text)).check(matches(withText("I spend 50 coins for my team.")));
    }

    @Test
    public void ISpendNothingIsShown(){
        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_power_up)).perform(ViewActions.click());

        //Check that if we swipe on the bar, we choose 80
        onView(ViewMatchers.withId(R.id.power_up_seek_bar)).perform(ViewActions.swipeLeft());
        onView(ViewMatchers.withId(R.id.power_up_spend_text)).check(matches(withText("I spend literally nothing for my team.")));
    }

    @Test
    public void CanSpendMoney(){
        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_power_up)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.power_up_seek_bar)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.power_up_button)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.power_up_money)).check(matches(withText("Money: 25")));
        onView(ViewMatchers.withId(R.id.power_up_fund)).check(matches(withText("Teams fund: 95")));
    }
}
