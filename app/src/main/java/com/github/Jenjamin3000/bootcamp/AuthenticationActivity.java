package com.github.Jenjamin3000.bootcamp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class AuthenticationActivity extends AppCompatActivity {

    //Create an ActivityResultLauncher which registers a callback for the FirebaseUI Activity result contract
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private Button submitButton;
    private RadioGroup mRadioGroup;

    private boolean authOK = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        //Directly launch the intent
        signInLauncher.launch(signInIntent);

        setContentView(R.layout.activity_authentication);

        mRadioGroup = findViewById(R.id.radioGroup);

        submitButton = findViewById(R.id.authSubmitButton);


        submitButton.setEnabled(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to main activity
                if(authOK) {
                    Section section;
                    switch(mRadioGroup.getCheckedRadioButtonId()){
                        case R.id.radioButtonArchi:
                            section = Section.Archi;
                            break;
                        case R.id.radioButtonGC:
                            section = Section.GC;
                            break;
                        case R.id.radioButtonSIE:
                            section = Section.SIE;
                            break;
                        case R.id.radioButtonIN:
                            section = Section.IN;
                            break;
                        case R.id.radioButtonSC:
                            section = Section.SC;
                            break;
                        case R.id.radioButtonCH:
                            section = Section.CH;
                            break;
                        case R.id.radioButtonMA:
                            section = Section.MA;
                            break;
                        case R.id.radioButtonPH:
                            section = Section.PH;
                            break;
                        default:
                            section = Section.MECA;
                    }
                    Toast.makeText(AuthenticationActivity.this, String.valueOf(section.toString()), Toast.LENGTH_SHORT).show();
                    User.setSection(section);

                    FireDatabase.initUser(section);

                    goToMainActivity();
                }
            }
        });
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

            authOK = true;

            verifyIfSectionKnown();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSectionClicked(View view){
        submitButton.setEnabled(true);
    }

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
}
