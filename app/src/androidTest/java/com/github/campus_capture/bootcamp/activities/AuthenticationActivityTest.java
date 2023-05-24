package com.github.campus_capture.bootcamp.activities;

import static androidx.test.espresso.Espresso.onView;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

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
import com.google.firebase.database.FirebaseDatabase;

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
    private final static String DUMB_USER = "dumb.user@epfl.ch";




    @Rule
    public ActivityScenarioRule<AuthenticationActivity> testRule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    /**
     * Setup the emulator and ensures that no user is already signed in in the app.
     */
    @BeforeClass
    public static void setup() {
        //Set emulators
        AppContext context = ApplicationProvider.getApplicationContext();
        FirebaseDatabase database = context.getFirebaseDB();
        context.getFirebaseAuth().useEmulator("10.0.2.2", 9099);

        // TODO: Find a way to know when the emulator is active or not
        //context.getFirebaseDB().useEmulator("10.0.2.2", 9000);

        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("has_voted").setValue(false);
        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("section").setValue("IN");


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
        clickOn(R.id.register_already_registered_button);
        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(FALSE_EMAIL));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        clickOn(R.id.login_button);

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));


    }

    @Test
    public void cannotAuthenticateIfNotYetRegistered() {
        //Go to login screen
        clickOn(R.string.register_already_registered_text);

        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(UNREG_EMAIL));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        clickOn(R.string.login_string);

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
        onView(ViewMatchers.withId(R.id.register_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Agree the TOS
        clickOn("I agree");

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
        onView(ViewMatchers.withId(R.id.register_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

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
        onView(ViewMatchers.withId(R.id.register_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        clickOn("I agree");

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void AuthenticateWorks() {

        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD_VER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Assert that an intent was launched
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));

    }

    @Test
    public void AuthenticateDoesNotWorksIfEmailNotVerified() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button))
                .perform(ViewActions.scrollTo())
                
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

    }

    @Test
    public void SpectatorWorks() {
        //Click the sign in button
        onView(ViewMatchers.withId(R.id.register_spectator_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

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
        onView(ViewMatchers.withId(R.id.register_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        clickOn("No");

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
        onView(ViewMatchers.withId(R.id.register_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void AutomaticallyLoggedIfAlreadyIn() {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD_VER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Relaunch the activity
        Espresso.pressBack();

        ActivityScenario.launch(AuthenticationActivity.class);

        //Assert that two MainActivity intents were launched
        List<Intent> theIntents = Intents.getIntents();
        assertThat(theIntents.size(), is(3));
        //Tests if the first login was successful
        assertThat(theIntents.get(0).getComponent().getClassName(), is(MainActivity.class.getName()));
        //Tests if, after the closing, the AuthenticationActivity was relaunched
        assertThat(theIntents.get(1).getComponent().getClassName(), is(AuthenticationActivity.class.getName()));
        //Tests if the user is automatically redirected to the MainActivity
        assertThat(theIntents.get(2).getComponent().getClassName(), is(MainActivity.class.getName()));
    }

    @Test
    public void CanGoToLoginAndThenComeBackToRegister(){
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Come back to register screen
        onView(ViewMatchers.withId(R.id.login_actually_no_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

        //Assert that the state is still working
        assertTrue(testRule.getScenario().getState().isAtLeast(Lifecycle.State.STARTED));
    }

    @Test
    public void CanChangePassword(){
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Come back to register screen
        onView(ViewMatchers.withId(R.id.login_password_forgotten_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Enter mail
        onView(ViewMatchers.withId(R.id.change_password_email_address)).perform(ViewActions.typeText(DUMB_USER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click on the send mail button
        onView(ViewMatchers.withId(R.id.send_mail_button))
                .perform(ViewActions.scrollTo())
                .perform(ViewActions.click());

        //Assert that the state is still working and we did not launch any intent
        assertTrue(testRule.getScenario().getState().isAtLeast(Lifecycle.State.STARTED));
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }
}
