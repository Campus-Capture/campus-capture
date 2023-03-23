package com.github.campus_capture.bootcamp;

import static android.content.ContentValues.TAG;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.firebase.FireDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityRuleFragmentTest {

    static FirebaseDatabase database = null;

    @Rule
    public ActivityScenarioRule<MainActivity> testRule = new ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void init_firebase_emulator() {

        try{
            AppContext context = (AppContext)ApplicationProvider.getApplicationContext();
            database = context.getFirebaseDB();
            database.useEmulator("127.0.0.1", 9000);

            //check for connection
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {
                        Log.d(TAG, "connected");
                    } else {
                        Log.d(TAG, "not connected");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Listener was cancelled");
                }
            });

        } catch (Exception e) {
            Log.e("Error init DB", e.toString());
        }
    }

    @Before
    public void clear_firebase_database(){
        database.getReference().setValue(null);
    }

    @Ignore //TODO comment out once firebase emulator is setup on CI
    @Test
    public void RulesFragmentShowTheRightContent() {
        AppContext context = (AppContext)ApplicationProvider.getApplicationContext();

        database.getReference().child("rules").setValue("The rules stocked in firebase emulator...");

        Intents.init();

        onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.nav_rules))
                .perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.rules_text))
                .check(matches(withText(containsString("The rules stocked in firebase emulator..."))));

        Intents.release();
    }
}
