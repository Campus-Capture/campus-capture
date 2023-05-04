package com.github.campus_capture.bootcamp.fragments;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private Button actually_no_button;
    private Button login_button;
    private Button password_forgotten_button;
    private EditText email;
    private String emailText;
    private EditText password;
    private String passwordText;
    private FirebaseAuth mAuth;
    private SharedPreferences mSharedPreferences;
    private final boolean firstLogin;
    /**
     * Constructor
     * @param email Email already entered
     * @param password Password already entered
     */
    public SignInFragment(String email, String password, boolean firstLogin) {
        // Required empty public constructor
        emailText = email;
        passwordText = password;
        this.firstLogin = firstLogin;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        // Init buttons
        actually_no_button = view.findViewById(R.id.login_actually_no_button);
        login_button = view.findViewById(R.id.login_button);
        password_forgotten_button = view.findViewById(R.id.login_password_forgotten_button);

        // Init texts
        email = view.findViewById(R.id.login_email_address);
        password = view.findViewById(R.id.login_password);

        //Set text
        email.setText(emailText);
        password.setText(passwordText);

        // Init Auth (Authenticater)
        AppContext context = AppContext.getAppContext();
        mAuth = context.getFirebaseAuth();

        // Init listeners on the buttons
        setActuallyNoButtonListener();
        setLoginButtonListener();
        setPasswordForgottenListener();
        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Sets the OnClickListener on the "actually_no_button"
     */
    private void setActuallyNoButtonListener(){
        actually_no_button.setOnClickListener(view -> goToRegisterFragment());
    }

    /**
     * Sets the OnClickListener on the "login_button"
     */
    private void setLoginButtonListener(){
        login_button.setOnClickListener(view -> loginClicked());
    }

    /**
     * Does actions when login_button is clicked
     */
    private void loginClicked(){

        // Update the email and password texts
        setEditTextToString();

        //If email ends with "@epfl.ch" accords authentication. Otherwise, show a message.
        if(emailText.endsWith("@epfl.ch")){
            authenticate();
        } else {
            Toast.makeText(getActivity(), "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Starts the authentication process
     */
    private void authenticate(){
        // Signs in the user in Firebase
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this::onCompleteLoginListenerContent);
    }

    /**
     * OnCompleteListener of the login_button
     * @param task
     */
    private void onCompleteLoginListenerContent(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Sign in success, set the signed-in user's information and go to main
            FirebaseUser user = mAuth.getCurrentUser();

            if(user.isEmailVerified()) {

                storeUserInfo(user.getUid());

                // Goes to MainActivity
                goToMainActivity();
            } else {
                Toast.makeText(getActivity(), "Please, verify your email.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.getException());
            Toast.makeText(getActivity(), "Authentication failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets the OnClickListener on the "password_forgotten_button"
     */
    private void setPasswordForgottenListener(){
        password_forgotten_button.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "That is sad", Toast.LENGTH_SHORT).show();
            goToChangePasswordFragment();
        });

    }

    /**
     * Directly goes to main
     */
    private void goToMainActivity(){
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }

    /**
     * Update the emailText and passwordText to be consistent to the current values
     */
    private void setEditTextToString(){
        emailText = email.getText().toString();
        passwordText = password.getText().toString();
    }

    /**
     * Method which opens the register fragment
     */
    private void goToRegisterFragment()
    {
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new RegisterFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }

    /**
     * Method which opens the change password fragment
     */
    private void goToChangePasswordFragment()
    {
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new ResetPasswordFragment(email.getText().toString()));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }

    private void storeUserInfo(String uid) {
        CompletableFuture<Section> futureSection = new CompletableFuture<>();
        if(firstLogin) {
            // Fetch section info from User class
            Section section = User.getSection();
            storeUserOnDB(uid, section);
            futureSection.complete(section);
        } else {
            // Fetch section info from database
            futureSection = (new FirebaseBackend()).getUserSection(uid)
                                                   .thenApply(s -> {
                                                       initUser(uid, s);
                                                       return s;
                                                   });
        }

        // in both cases, we store on disk
        futureSection.thenAccept(s -> storeUserOnDisk(uid, s));

    }

    private void storeUserOnDB(String uid, Section section) {
        FirebaseBackend backend = new FirebaseBackend();
        backend.setUserSection(uid, section);
    }

    private void storeUserOnDisk(String uid, Section section) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("UID", uid);
        editor.putString("Section", section.toString());
        editor.apply();
    }

    private void initUser(String uid, Section section) {
        User.setUid(uid);
        User.setSection(section);
    }


}