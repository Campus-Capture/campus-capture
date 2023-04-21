package com.github.campus_capture.bootcamp.activities;

import static androidx.test.espresso.Espresso.onView;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


@RunWith(AndroidJUnit4.class)
public class AuthenticationActivityTest {

    private final static String FALSE_EMAIL = "roger.federer@gmail.com";
    private final static String REG_EMAIL = "rafael.nadal@epfl.ch";
    private final static String UNREG_EMAIL = "roger.federer@epfl.ch";
    private final static String ALREADY_IN_EMAIL = "andy.murray@epfl.ch";
    private final static String ALREADY_IN_EMAIL_VER = "stefanos.tsitsipas@epfl.ch";
    private final static String PASSWORD = "NadalMyLove";
    private final static String ALREADY_IN_PASSWORD = "ScotlandBelongsToItself";
    private final static String ALREADY_IN_PASSWORD_VER = "NoMoneyNoProblem";
    private final static String SMALL_PASSWORD = "pass";




    @Rule
    public ActivityScenarioRule<AuthenticationActivity> testRule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    /**
     * Setup the emulator and ensures that no user is already signed in in the app.
     */
    @BeforeClass
    public static void setup() {
        //Set emulators
        AppContext context = ApplicationProvider.getApplicationContext();
        context.getFirebaseAuth().useEmulator("10.0.2.2", 9099);
        context.getFirebaseAuth().signOut();
    }

    /**
     * Init the Intents recorder before each test.
     */
    @Before
    public void singleSetup(){
        Intents.init();
    }

    /**
     * Release the Intents recorder and sign out the user after each test.
     */
    @After
    public void SingleCleanup(){
        Intents.release();
        AppContext context = ApplicationProvider.getApplicationContext();
        context.getFirebaseAuth().signOut();
    }

    @Test
    public void cannotAuthenticateWithNotEPFLEmail() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());
        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(FALSE_EMAIL));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));


    }

    @Test
    public void cannotAuthenticateIfNotYetRegistered() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(UNREG_EMAIL));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

    }

    @Test
    public void cannotRegisterIfAlready() {
        //Fill email and password
        onView(ViewMatchers.withId(R.id.register_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL));
        onView(ViewMatchers.withId(R.id.register_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();


        //Click the register button
        onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());

        //Agree the TOS
        clickOn("I agree");

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

    }

    @Test
    public void cannotRegisterIfNOTEPFLEmail() {
        //Fill email and password
        onView(ViewMatchers.withId(R.id.register_email_address)).perform(ViewActions.typeText(FALSE_EMAIL));
        onView(ViewMatchers.withId(R.id.register_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the register button
        onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

    }



    @Test
    public void canRegister() {

        //Fill email and password
        onView(ViewMatchers.withId(R.id.register_email_address)).perform(ViewActions.typeText(REG_EMAIL));
        onView(ViewMatchers.withId(R.id.register_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());

        clickOn("I agree");

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void AuthenticateWorks() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD_VER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that an intent was launched
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));

    }

    @Test
    public void AuthenticateDoesNotWorksIfEmailNotVerified() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

    }

    @Test
    public void SpectatorWorks() {
        //Click the sign in button
        onView(ViewMatchers.withId(R.id.register_spectator_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that an intent was launched
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void CantConnectIfTOSNotAgreed() {
        onView(ViewMatchers.withId(R.id.register_email_address)).perform(ViewActions.typeText(REG_EMAIL));
        onView(ViewMatchers.withId(R.id.register_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());

        clickOn("No");

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void CannotRegisterIfPasswordTooShort(){
        onView(ViewMatchers.withId(R.id.register_email_address)).perform(ViewActions.typeText(REG_EMAIL));
        onView(ViewMatchers.withId(R.id.register_password)).perform(ViewActions.typeText(SMALL_PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.register_button)).perform(ViewActions.click());

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void AutomaticallyLoggedIfAlreadyIn() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD_VER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        //Wait 3 seconds
        //Thread.sleep(SECONDS.toMillis(3));

        //Relaunch the activity
        Espresso.pressBack();
        //Thread.sleep(SECONDS.toMillis(1));

        //AuthenticationActivityTestRule.launchActivity(null);
        ActivityScenario.launch(AuthenticationActivity.class);

        //Thread.sleep(SECONDS.toMillis(1));

        //Assert that two MainActivity intents were launched
        List<Intent> theIntents = Intents.getIntents();
        assertThat(theIntents.size(), is(3));
        assertThat(theIntents.get(0).getComponent().getClassName(), is(MainActivity.class.getName()));
        assertThat(theIntents.get(1).getComponent().getClassName(), is(AuthenticationActivity.class.getName()));
        assertThat(theIntents.get(2).getComponent().getClassName(), is(MainActivity.class.getName()));
    }

    @Test
    public void CanGoToLoginAndThenComeBackToRegister(){
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Come back to register screen
        onView(ViewMatchers.withId(R.id.login_actually_no_button)).perform(ViewActions.click());

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

        //Assert that the state is still working
        assertTrue(testRule.getScenario().getState().isAtLeast(Lifecycle.State.STARTED));
    }
}
