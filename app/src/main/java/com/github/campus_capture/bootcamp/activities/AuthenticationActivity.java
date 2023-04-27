package com.github.campus_capture.bootcamp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.fragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    SharedPreferences mSharedPreferences;


    /**
     * Init the buttons, EditTexts and the FirebaseAuth.
     * @param savedInstanceState
     * This is the savedInstanceState  __  /. .\  __
     *                                   \_\ _ /_/
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Init Auth (Authenticater)
        AppContext context = AppContext.getAppContext();
        FirebaseAuth auth = context.getFirebaseAuth();

        mSharedPreferences = getPreferences(MODE_PRIVATE);

        // Check if user is signed in (non-null) and go to main if yes.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            User.setSection(Section.valueOf(mSharedPreferences.getString("Section", "NONE")));
            User.setUid(mSharedPreferences.getString("UID", null));
            goToMainActivity();
        } else {
            goToRegisterFragment();
        }

    }

    /**
     * On start, verify if the user is already logged in. If yes, go to main, otherwise, continue on the AuthenticationActivity.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    private void goToMainActivity(){
        Intent mainIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    /**
     * Method which opens the register fragment
     */
    private void goToRegisterFragment()
    {
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new RegisterFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }
}