package com.github.Jenjamin3000.bootcamp;

import android.app.Application;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import  static androidx.test.espresso.Espresso.onView;


@RunWith(AndroidJUnit4.class)
public class GreetingActivityTest {
    @Test
    public void SimpleTest(){
        Intent myIntent = new Intent(ApplicationProvider.getApplicationContext(), GreetingActivity.class);
        ActivityScenario.launch(myIntent).close();
        onView(ViewMatchers.withId(R.id.greetingText)).noActivity();
    }
}
