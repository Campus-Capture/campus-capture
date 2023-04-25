package com.github.campus_capture.bootcamp.activities;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
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
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.TOS;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {

    private Button login_button;
    private Button register_button;
    private Button spectator_button;
    private EditText email;
    private String emailText;
    private EditText password;
    private String passwordText;
    private FirebaseAuth mAuth;


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


        // Init buttons
        login_button = findViewById(R.id.login_confirm_button);
        register_button = findViewById(R.id.login_register_button);
        spectator_button = findViewById(R.id.login_spectator_button);

        // Init texts
        email = findViewById(R.id.login_email_address);
        password = findViewById(R.id.login_password);

        // Init Auth (Authenticater)
        AppContext context = (AppContext) getApplicationContext();
        mAuth = context.getFirebaseAuth();

        // Init listeners on the buttons
        setLoginButtonListener();
        setRegisterButtonListener();
        setSpectatorButton_listener();

    }

    /**
     * On start, verify if the user is already logged in. If yes, go to main, otherwise, continue on the AuthenticationActivity.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and go to main if yes.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            goToMainActivity();
        }
    }

    private void setSpectatorButton_listener(){
        spectator_button.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void setRegisterButtonListener(){
        register_button.setOnClickListener(view -> registerClicked());
    }

    private void registerClicked(){
        setEditTextToString();


        if(emailText.endsWith("@epfl.ch")){
            if(passwordText.length() >= 6) {
                displayTos();
            } else {
                Toast.makeText(this, "Your password must be at least 6 characters long", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    private void register(){
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, this::onCompleteRegisterListenerContent);
    }

    private void setLoginButtonListener(){
        login_button.setOnClickListener(view -> loginClicked());
    }

    private void loginClicked(){
        setEditTextToString();

        //If email ends with "@epfl.ch" accords authentication. Otherwise, show a message.
        if(emailText.endsWith("@epfl.ch")){
            authenticate();
        } else {
            Toast.makeText(this, "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    private void authenticate(){
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, this::onCompleteLoginListenerContent);
    }

    private void goToMainActivity(){
        Intent mainIntent = new Intent(AuthenticationActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void setEditTextToString(){
        emailText = email.getText().toString();
        passwordText = password.getText().toString();
    }

    private void onCompleteRegisterListenerContent(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Register success, send verification mail.
            FirebaseUser user = mAuth.getCurrentUser();

            user.sendEmailVerification()
                    .addOnSuccessListener(unused -> Toast.makeText(AuthenticationActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(AuthenticationActivity.this, "Verification email not sent", Toast.LENGTH_SHORT).show());

            BackendInterface backendInterface = new FirebaseBackend();
            // TODO allow user to choose section
            backendInterface.initUserInDB(user.getUid(), Section.IN);

        } else {
            // If register fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.getException());
            Toast.makeText(AuthenticationActivity.this, "Register failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void onCompleteLoginListenerContent(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Sign in success, set the signed-in user's information and go to main
            FirebaseUser user = mAuth.getCurrentUser();

            User.setUid(user.getUid());

            if(user.isEmailVerified()) {
                goToMainActivity();
            } else {
                Toast.makeText(AuthenticationActivity.this, "Please, verify your email.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.getException());
            Toast.makeText(AuthenticationActivity.this, "Authentication failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayTos() {
        new AlertDialog.Builder(this)
                .setTitle("License agreement")
                .setPositiveButton("I agree", (dialog, which) -> {
                    TOS.asAgreed = true;
                    register();
                })
                .setNegativeButton("No", null)
                .setMessage(TOS.TEXT)
                .show();
    }
}