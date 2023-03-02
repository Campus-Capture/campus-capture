package com.github.Jenjamin3000.bootcamp;

import static androidx.test.espresso.Espresso.onView;

import static org.hamcrest.CoreMatchers.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)

public class BoredActivityTest {

    @Rule
    public ActivityScenarioRule<BoredActivity> testRule = new ActivityScenarioRule<BoredActivity>(BoredActivity.class);

    @Test
    public void FetchOnCreateTest() throws InterruptedException {

        //TODO refactor with Dep inj using hilt and a mock server
        TimeUnit.SECONDS.sleep(1);
        onView(ViewMatchers.withId(R.id.activityText)).check(ViewAssertions.matches(not(ViewMatchers.withText("Error fetching activity"))));
    }

    @Test
    public void FetchOnClickTest() throws InterruptedException {

        //TODO refactor with Dep inj using hilt and a mock server
        TimeUnit.SECONDS.sleep(1);
        onView(ViewMatchers.withId(R.id.fetchActivityButton)).perform(ViewActions.click());
        TimeUnit.SECONDS.sleep(1);
        onView(ViewMatchers.withId(R.id.activityText)).check(ViewAssertions.matches(not(ViewMatchers.withText("Error fetching activity"))));
    }
}