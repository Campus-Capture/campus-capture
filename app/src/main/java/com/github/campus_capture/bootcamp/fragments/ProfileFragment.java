package com.github.campus_capture.bootcamp.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.AuthenticationActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {



    AdapterView.OnItemSelectedListener sectionSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            section = Section.values()[(int) id];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private final AuthenticationActivity currentActivity;
    private Section section;
    private final String emailText;
    private final String passwordText;
    private FirebaseAuth mAuth;

    public ProfileFragment(AuthenticationActivity activity, String emailText, String passwordText) {
        // Required empty public constructor
        this.emailText = emailText;
        this.passwordText = passwordText;
        currentActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Retrieve the views from the layout
        Spinner sectionSpinner = view.findViewById(R.id.profile_section_spinner);
        Button confirmButton = view.findViewById(R.id.profile_confirm_button);

        // Init Auth (Authenticater)
        AppContext context = AppContext.getAppContext();
        mAuth = context.getFirebaseAuth();

        // Display the name of the player
//        String name = userRef.child("email").toString();
//        userName.setText(name);

        // Display and set listener
//        showSelection(true);
        sectionSpinner.setOnItemSelectedListener(sectionSpinnerListener);
        confirmButton.setOnClickListener(this::displayAlert);

        return view;
    }

    private void displayAlert(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                     public void onClick(DialogInterface dialog, int which) {
                        User.setSection(section);
                        register();
                    }
                })
                .setNegativeButton("No", null)
                .setMessage("Once selected, the selected section will be permanent ! Do you want to proceed ?")
                .show();
    }

    /**
     * Launch the register protocol: create a new user and go to sign in fragment.
     */
    private void register(){
        //Create user in Firebase
        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(this::onCompleteRegisterListenerContent);
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

            //Go to sign in fragment
            currentActivity.goToSignInFragment(emailText, passwordText, true);

        } else {
            // If register fails, display a message to the user.
            Log.w(TAG, "signInWithEmail:failure", task.getException());
            Toast.makeText(getActivity(), "Register failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

}