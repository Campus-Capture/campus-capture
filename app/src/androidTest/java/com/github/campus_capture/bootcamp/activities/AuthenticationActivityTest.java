package com.github.campus_capture.bootcamp.activities;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.adevinta.android.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import static java.util.concurrent.TimeUnit.SECONDS;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.adevinta.android.barista.interaction.BaristaClickInteractions;
import com.adevinta.android.barista.interaction.BaristaMenuClickInteractions;
import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


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
    private final static String ALREADY_REGISTER_EMAIL_NOT_LOGGED = "regis.nologson@epfl.ch";
    private final static String ALREADY_REGISTER_PASSWORD_NOT_LOGGED = "IRegisteredForNowButIWillCloseTheAppJustAfterBecauseIAmWeird";
    private final static String ALREADY_REGISTER_EMAIL_NOT_LOGGED_VER = "regis-ver.nologson@epfl.ch";
    private final static String ALREADY_REGISTER_PASSWORD_NOT_LOGGED_VER = "IRegisteredAndVerifiedForNowButIWillCloseTheAppJustAfterBecauseIAmWeird";

    @Rule
    public ActivityScenarioRule<AuthenticationActivity> testRule = new ActivityScenarioRule<>(AuthenticationActivity.class);

    static FirebaseDatabase database = null;
    static FirebaseAuth auth = null;


    /**
     * Setup the emulator and ensures that no user is already signed in in the app.
     */
    @BeforeClass
    public static void setup() {
        //Set emulators
        AppContext context = AppContext.getAppContext();
        database = context.getFirebaseDB();
        auth = context.getFirebaseAuth();

        try{
            Log.d("AuthenticationActivityTest", "enable auth emulator");
            auth.useEmulator("10.0.2.2", 9099);
        } catch (Exception e) {
            Log.e("AuthenticationActivityTest", e.toString());
        }


        try{
            Log.d("AuthenticationActivityTest", "enable database emulator");
            database.useEmulator("10.0.2.2", 9000);

        } catch (Exception e) {
            Log.e("AuthenticationActivityTest", e.toString());
        }

        auth.signOut();
    }

    /**
     * Init the Intents recorder before each test.
     */
    @Before
    public void singleSetup(){
        database.getReference().setValue(null);
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

        onIdle();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

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
        onIdle();

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

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void AuthenticateWorks() throws InterruptedException {

        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("has_voted").setValue(false);
        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("section").setValue("IN");
        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("money").setValue(0);

        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Fill email and password
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD_VER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        Thread.sleep(2000);

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

        //Assert that no intent was launched
        assertThat(Intents.getIntents().isEmpty(), is(true));

    }

    @Test
    public void SpectatorWorks() {
        //Click the sign in button
        onView(ViewMatchers.withId(R.id.register_spectator_button)).perform(ViewActions.click());

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

        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("has_voted").setValue(false);
        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("section").setValue("IN");
        database.getReference().child("Users").child("pp5iYDmG4tRfoLKjWvRf0s1bVJc8").child("money").setValue(0);

        Intents.init();

        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_IN_EMAIL_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_IN_PASSWORD_VER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click the sign in button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());

        //Relaunch the activity
        Espresso.pressBack();
        ActivityScenario.launch(AuthenticationActivity.class).close();


        //Wait for the state to be idle and then test
        Espresso.onIdle((Callable<Function<Void, Void>>) () -> (Void nothing) -> {
            //Assert that two MainActivity intents were launched
            List<Intent> theIntents = Intents.getIntents();
            assertThat(theIntents.size(), is(3));
            //Tests if the first login was successful
            assertThat(theIntents.get(0).getComponent().getClassName(), is(MainActivity.class.getName()));
            //Tests if, after the closing, the AuthenticationActivity was relaunched
            assertThat(theIntents.get(1).getComponent().getClassName(), is(AuthenticationActivity.class.getName()));
            //Tests if the user is automatically redirected to the MainActivity
            assertThat(theIntents.get(2).getComponent().getClassName(), is(MainActivity.class.getName()));
            return null;
        });

        Intents.release();
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

    @Test
    public void CanChangePassword(){
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Come back to register screen
        onView(ViewMatchers.withId(R.id.login_password_forgotten_button)).perform(ViewActions.click());

        //Enter mail
        onView(ViewMatchers.withId(R.id.change_password_email_address)).perform(ViewActions.typeText(DUMB_USER));

        //Close keyboard
        Espresso.closeSoftKeyboard();

        //Click on the send mail button
        onView(ViewMatchers.withId(R.id.send_mail_button)).perform(ViewActions.click());

        //Assert that the state is still working and we did not launch any intent
        assertTrue(testRule.getScenario().getState().isAtLeast(Lifecycle.State.STARTED));
        assertThat(Intents.getIntents().isEmpty(), is(true));
    }

    @Test
    public void canResendMail() throws InterruptedException {
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Enter the name of the already registered but not verified user
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_REGISTER_EMAIL_NOT_LOGGED));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_REGISTER_PASSWORD_NOT_LOGGED));

        Espresso.closeSoftKeyboard();

        // Click on the login button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());
        onIdle();

        // Wait for the button to be redisplayed
        Thread.sleep(SECONDS.toMillis(1));

        // Click on the button
        onView(ViewMatchers.withId(R.id.login_resend_button)).perform(ViewActions.click());

        // Wait for the button to be redisplayed
        Thread.sleep(SECONDS.toMillis(12));

        // Check that the resend button is visible
        onView(ViewMatchers.withId(R.id.login_resend_button)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void canGoOnProfile(){
        //Go to login screen
        onView(ViewMatchers.withId(R.id.register_already_registered_button)).perform(ViewActions.click());

        //Enter the name of the already registered but not verified user
        onView(ViewMatchers.withId(R.id.login_email_address)).perform(ViewActions.typeText(ALREADY_REGISTER_EMAIL_NOT_LOGGED_VER));
        onView(ViewMatchers.withId(R.id.login_password)).perform(ViewActions.typeText(ALREADY_REGISTER_PASSWORD_NOT_LOGGED_VER));

        Espresso.closeSoftKeyboard();

        // Click on the login button
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click());
        onIdle();

        onView(ViewMatchers.withText("Profile")).check(matches(isDisplayed()));
    }
}
