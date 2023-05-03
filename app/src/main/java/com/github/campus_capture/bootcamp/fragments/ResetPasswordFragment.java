package com.github.campus_capture.bootcamp.fragments;

import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {

    private String iniEmailText;
    private EditText email;
    private String emailText = "";
    private Button sendMailButton;
    private final FirebaseAuth mAuth;

    public ResetPasswordFragment(String email) {
        iniEmailText = email;

        mAuth = AppContext.getAppContext().getFirebaseAuth();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        email = view.findViewById(R.id.change_password_email_address);

        sendMailButton = view.findViewById(R.id.send_mail_button);

        email.setText(iniEmailText);

        sendMailButton.setOnClickListener(view12 -> sendMailButtonListener());

        return view;
    }

    private void sendMailButtonListener(){
        emailText = email.getText().toString();
        mAuth.sendPasswordResetEmail(emailText);

        Toast.makeText(getActivity(), "You can change your password with the link you receive at "+emailText+".", Toast.LENGTH_LONG).show();

        goToSignInFragment(emailText);
    }

    /**
     * Method which opens the sign in fragment
     */
    private void goToSignInFragment(String email){
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerViewAuthentication, new SignInFragment(email, "", false));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
    }
}