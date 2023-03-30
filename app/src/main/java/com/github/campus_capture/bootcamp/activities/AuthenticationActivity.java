package com.github.campus_capture.bootcamp.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    private Button login_button;
    private Button register_button;
    private EditText email;
    private String emailText;
    private EditText password;
    private String passwordText;
    private FirebaseAuth mAuth;


    /**
     * Init the buttons, EditTexts and the FirebaseAuth.
     * @param savedInstanceState
     * The is the savedInstanceState   _  /. .\  _
     *                                  \_\ _ /_/
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        // Init buttons
        login_button = findViewById(R.id.login_confirm_button);
        register_button = findViewById(R.id.login_register_button);

        // Init texts
        email = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword2);

        // Init Auth (Authenticater)
        AppContext context = (AppContext) getApplicationContext();
        mAuth = context.getFirebaseAuth();

        // Init listeners on the buttons
        setLoginButtonListener();
        setRegisterButtonListener();
    }

    /**
     * On start, verify if the user is already logged in. If yes, go to main, otherwise, continue on the AuthenticationActivity.
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and go to main if yes.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToMainActivity();
        }
    }

    private void setRegisterButtonListener(){
        register_button.setOnClickListener(view -> registerClicked());
    }

    private void registerClicked(){
        emailText = email.getText().toString();
        passwordText = password.getText().toString();

        if(emailText.endsWith("@epfl.ch")){
            register();
        } else {
            Toast.makeText(this, "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(){
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, set the signed-in user's information and go to main
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        if(user.getDisplayName()!=null) {
                            User.setName(user.getDisplayName());
                        }
                        User.setUid(user.getUid());
                        goToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(AuthenticationActivity.this, "Register failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoginButtonListener(){
        login_button.setOnClickListener(view -> loginClicked());
    }

    private void loginClicked(){
        //Get the email and the password
        emailText = email.getText().toString();
        passwordText = password.getText().toString();

        //If email ends with "@epfl.ch" accords authentication. Otherwise, show a message.
        if(emailText.endsWith("@epfl.ch")){
            authenticate();
        } else {
            Toast.makeText(this, "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    private void authenticate(){
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, set the signed-in user's information and go to main
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        if(user.getDisplayName()!=null) {
                            User.setName(user.getDisplayName());
                        }
                        User.setUid(user.getUid());

                        goToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthenticationActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMainActivity(){
        Intent mainIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }
}
