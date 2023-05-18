package com.github.campus_capture.bootcamp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.AuthenticationActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;

/**
 * This class represent the behaviour of the profil fragment.
 */
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

    /**
     *
     * This fragment is displayed after the registration and before the
     * login, thus it must keep track of what the given credentials was.
     * @param activity The parent activity
     * @param emailText User Mail
     * @param passwordText User password
     */
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

        // Display the name of the player
//        String name = userRef.child("email").toString();
//        userName.setText(name);

        // Display and set listener
//        showSelection(true);
        sectionSpinner.setOnItemSelectedListener(sectionSpinnerListener);
        confirmButton.setOnClickListener(this::displayAlert);

        return view;
    }

    /**
     * Display on top of everything the warning for the section selection (it is permanent)
     * @param view The view
     */
    private void displayAlert(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.profile_alert_title)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                     public void onClick(DialogInterface dialog, int which) {
                        User.setSection(section);
                        currentActivity.goToSignInFragment(emailText, passwordText, true);
                    }
                })
                .setNegativeButton("No", null)
                .setMessage(R.string.profile_selection_warning)
                .show();
    }

}