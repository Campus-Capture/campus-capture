package com.github.campus_capture.bootcamp.fragments;

import static androidx.core.content.res.TypedArrayUtils.getText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.map.MapScheduler;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RunWith(AndroidJUnit4.class)
public class MapTimerTests {

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionLocation = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @After
    public void close()
    {
        Intents.release();
    }

    @Test
    public void planningBannerCorrectlyDisplayed()
    {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withId(R.id.planningPhaseBanner)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.takeoverPhaseBanner)).check(matches(not(isDisplayed())));
    }

    @Test
    public void planningBannerTimerBehavesCorrectly() throws ParseException, InterruptedException {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 30);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        String comp1 = getText(ViewMatchers.withId(R.id.planningBannerText));
        Date date1 = new SimpleDateFormat("mm:ss").parse(comp1.substring(33));
        assert date1 != null;
        String comp2 = "Planning phase - remaining time: " + new SimpleDateFormat("mm:ss").format(new Date(date1.getTime() - 5000));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.planningBannerText)).check(matches(withText(containsString(comp2))));
    }

    @Test
    public void takeoverBannerCorrectlyDisplayed()
    {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;

        Intents.init();

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.planningPhaseBanner)).check(matches(not(isDisplayed())));
        onView(ViewMatchers.withId(R.id.takeoverPhaseBanner)).check(matches(isDisplayed()));
    }

    @Test
    public void takeoverBannerTimerBehavesCorrectly() throws ParseException, InterruptedException {
        Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, 5);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        MapScheduler.overrideTime = true;
        MapScheduler.time = time;

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_maps)).perform(ViewActions.click());

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        String comp1 = getText(ViewMatchers.withId(R.id.takeoverBannerText));
        Date date1 = new SimpleDateFormat("mm:ss").parse(comp1.substring(33));
        assert date1 != null;
        String comp2 = "Takeover phase - remaining time: " + new SimpleDateFormat("mm:ss").format(new Date(date1.getTime() - 5000));

        Thread.sleep(5000);

        onView(ViewMatchers.withId(R.id.takeoverBannerText)).check(matches(withText(containsString(comp2))));
    }

    /**
     * Helper method to retrieve the text from a text view, since the time between opening the UI
     * and reading the value differs from the time instance injected
     * Taken from <a href="https://stackoverflow.com/questions/8833399/changing-java-date-one-hour-back">this SO post</a>
     * @param matcher the view we're trying to match
     * @return String
     */
    String getText(final Matcher<View> matcher) {
        final String[] stringHolder = { null };
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView)view; //Save, because of check in getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

}
