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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    public SignInFragment(String email, String password) {
        // Required empty public constructor
        emailText = email;
        passwordText = password;
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

    private void setActuallyNoButtonListener(){
        actually_no_button.setOnClickListener(view -> goToRegisterFragment());
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
            Toast.makeText(getActivity(), "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    private void authenticate(){
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this::onCompleteLoginListenerContent);
    }

    private void onCompleteLoginListenerContent(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Sign in success, set the signed-in user's information and go to main
            FirebaseUser user = mAuth.getCurrentUser();

            User.setUid(user.getUid());



            if(user.isEmailVerified()) {

                //TODO: Remove that and put an equivalent in the login screen!!!
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("Section", "IN");
                User.setSection(Section.IN);

                editor.putString("UID", User.getUid());
                editor.apply();
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

    private void setPasswordForgottenListener(){
        password_forgotten_button.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "That is sad", Toast.LENGTH_SHORT).show();
            goToChangePasswordFragment();
        });

    }

    private void goToMainActivity(){
        Intent mainIntent = new Intent(getActivity(), MainActivity.class);
        startActivity(mainIntent);
    }

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

}