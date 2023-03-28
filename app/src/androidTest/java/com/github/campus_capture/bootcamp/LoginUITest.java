package com.github.campus_capture.bootcamp;

import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.adevinta.android.barista.rule.BaristaRule;
import com.github.campus_capture.bootcamp.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginUITest {

    @Rule
    public BaristaRule<LoginActivity> loginActivity = BaristaRule.create(LoginActivity.class);

    @Test
    public void tryLogin() {
        loginActivity.launchActivity();

        clickOn("Log in");
    }
}
