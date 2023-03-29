package com.github.campus_capture.bootcamp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class AuthenticationActivity extends AppCompatActivity {

    //Create an ActivityResultLauncher which registers a callback for the FirebaseUI Activity result contract
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final boolean authOK = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        // Init buttons
        Button login_button = findViewById(R.id.login_confirm_button);

        //TODO : Add spectator mode
        Button spectate_button = findViewById(R.id.login_spectator_button);
        spectate_button.setOnClickListener(view -> goToMainActivity());

        setLoginButtonListener(login_button, signInIntent);
    }

    private void setLoginButtonListener(Button submitButton, Intent intent){
        submitButton.setOnClickListener(view -> signInLauncher.launch(intent));
    }

    private void goToMainActivity(){
        Intent mainIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            Toast.makeText(this, "Sign in succeeded, User: "+user.getUid(), Toast.LENGTH_SHORT).show();

            //Set the User
            User.setUid(user.getUid());
            User.setName(user.getDisplayName());

            goToMainActivity();

            //verifyIfSectionKnown();
        } else {
            // Sign in failed.
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    private void verifyIfSectionKnown(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users/"+User.getUid()+"/section");
        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null) {
                    goToMainActivity();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
    */
}
