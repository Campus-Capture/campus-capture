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
    private EditText newPassword;
    private EditText code;
    private Button sendMailButton;
    private Button changePasswordButton;
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
        newPassword = view.findViewById(R.id.change_password_text);
        code = view.findViewById(R.id.change_password_code);

        sendMailButton = view.findViewById(R.id.send_mail_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);

        email.setText(iniEmailText);

        sendMailButton.setOnClickListener(view12 -> {
            emailText = email.getText().toString();
            mAuth.sendPasswordResetEmail(emailText);

            changeLayoutObjectsVisibility();

            Toast.makeText(getActivity(), "Please, write the code you receive at "+emailText+".", Toast.LENGTH_LONG).show();
        });

        changePasswordButton.setOnClickListener(view1 -> {
            String codeText = code.getText().toString();
            String newPasswordText = newPassword.getText().toString();
            mAuth.confirmPasswordReset(codeText, newPasswordText).addOnSuccessListener(unused -> goToSignInFragment(emailText, newPasswordText));
        });

        return view;
    }

    private void changeLayoutObjectsVisibility(){
        email.setVisibility(View.GONE);
        sendMailButton.setVisibility(View.GONE);

        newPassword.setVisibility(View.VISIBLE);
        code.setVisibility(View.VISIBLE);
        changePasswordButton.setVisibility(View.VISIBLE);
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