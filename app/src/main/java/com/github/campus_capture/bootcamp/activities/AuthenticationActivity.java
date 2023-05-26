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
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.github.campus_capture.bootcamp.fragments.ProfileFragment;
import com.github.campus_capture.bootcamp.fragments.RegisterFragment;
import com.github.campus_capture.bootcamp.fragments.ResetPasswordFragment;
import com.github.campus_capture.bootcamp.fragments.SignInFragment;
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

        FirebaseBackend database = new FirebaseBackend();

        // Check if user had already registered and if the email is verified.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){

            //Check if the user had already logged in (is in the DB)
            database.isUserInDB(currentUser.getUid()).thenAccept((isIn) -> {
                if(isIn){
                    if(mSharedPreferences.contains(currentUser.getUid())){
                        // Fetch section info from disk
                        readUserInfoFromDisk(currentUser.getUid());
                    } else {
                        database.getUserSection(currentUser.getUid()).thenAccept((section -> {
                            mSharedPreferences.edit().putString("Section", section.name()).apply();
                            mSharedPreferences.edit().putString("UID", currentUser.getUid()).apply();
                            User.setSection(section);
                            User.setUid(currentUser.getUid());
                        }));
                    }
                    goToMainActivity();
                } else {
                    // Go to sign in fragment
                    goToSignInFragment(currentUser.getEmail(), "", false);
                }
            });
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

    /**
     * Directly go to main
     */
    public void goToMainActivity(){
        Intent mainIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    /**
     * Method which opens the register fragment
     */
    public void goToRegisterFragment()
    {
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new RegisterFragment(this));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }

    /**
     * Method which opens the sign in fragment
     */
    public void goToSignInFragment(String email, String password, boolean firstLogin){
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new SignInFragment(this, email, password, firstLogin));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }

    /**
     * Method which opens the change password fragment
     */
    public void goToChangePasswordFragment(String email)
    {
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new ResetPasswordFragment(this, email));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }

    /**
     * Method which opens the profile fragment
     */
    public void goToProfileFragment(){
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new ProfileFragment(this));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }

    private void readUserInfoFromDisk(String uid) {

        Section section = Section.valueOf(mSharedPreferences.getString("Section", "NONE"));
        User.setUid(uid);
        User.setSection(section);
    }
}