package com.github.campus_capture.bootcamp.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.github.campus_capture.bootcamp.authentication.TOS;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    private Button already_registered_button;
    private Button register_button;
    private Button spectator_button;
    private EditText email;
    private String emailText;
    private EditText password;
    private String passwordText;
    private FirebaseAuth mAuth;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // Init buttons
        already_registered_button = view.findViewById(R.id.register_already_registered_button);
        register_button = view.findViewById(R.id.register_button);
        spectator_button = view.findViewById(R.id.register_spectator_button);

        // Init texts
        email = view.findViewById(R.id.register_email_address);
        password = view.findViewById(R.id.register_password);

        // Init Auth (Authenticater)
        AppContext context = AppContext.getAppContext();
        mAuth = context.getFirebaseAuth();

        // Init listeners on the buttons
        setAlreadyRegisteredButtonListener();
        setRegisterButtonListener();
        setSpectatorButtonListener();
        // Inflate the layout for this fragment
        return view;
    }

    private void setRegisterButtonListener(){
        register_button.setOnClickListener(view -> registerClicked());
    }

    private void registerClicked(){
        setEditTextToString();


        //If email and password are valid, display TOS
        if(emailText.endsWith("@epfl.ch")){
            if(passwordText.length() >= 6) {
                displayTos();
            } else {
                Toast.makeText(getActivity(), "Your password must be at least 6 characters long", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(getActivity(), "You must enter a epfl address", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Launch the register protocol: create a new user and go to sign in fragment.
     */
    private void register(){
        //Create user in Fiebase
        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this::onCompleteRegisterListenerContent);

        //Go to sign in fragment
        goToSignInFragment(emailText, passwordText);
    }

    /**
     * CompleteListener of the user creation on Firebase
     * @param task Result of the action
     */
    private void onCompleteRegisterListenerContent(Task<AuthResult> task){
        if (task.isSuccessful()) {
            // Register success, send verification mail.
            FirebaseUser user = mAuth.getCurrentUser();

            // Send the verification mail
            user.sendEmailVerification()
                    .addOnSuccessListener(unused -> Toast.makeText(getActivity(), "Verification email sent", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Verification email not sent", Toast.LENGTH_SHORT).show());

        } else {
            // If register fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.getException());
            Toast.makeText(getActivity(), "Register failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display the TOS
     */
    private void displayTos() {
        // Display the dialog popup
        new AlertDialog.Builder(getActivity())
                .setTitle("License agreement")
                .setPositiveButton("I agree", (dialog, which) -> {
                    TOS.asAgreed = true;

                    // Start the register protocol when "I agree" clicked
                    register();
                })
                .setNegativeButton("No", null)
                .setMessage(TOS.TEXT)
                .show();
    }

    /**
     * Sets the Listener on the "already_registered_button"
     */
    private void setAlreadyRegisteredButtonListener() {
        already_registered_button.setOnClickListener(view -> goToSignInFragment("", ""));
    }

    /**
     * Sets the Listener on the "spectator_button"
     */
    private void setSpectatorButtonListener(){
        spectator_button.setOnClickListener(view -> {

            // Directly go to MainActivity
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Update the emailText and passwordText to be consistent with the current value.
     */
    private void setEditTextToString(){
        emailText = email.getText().toString();
        passwordText = password.getText().toString();
    }

    /**
     * Method which opens the sign in fragment
     */
    private void goToSignInFragment(String email, String password){
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new SignInFragment(email, password));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }
}