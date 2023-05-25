package com.github.campus_capture.bootcamp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.AuthenticationActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;

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
    private SharedPreferences mSharedPreferences;


    public ProfileFragment(AuthenticationActivity activity) {
        currentActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = currentActivity.getPreferences(Context.MODE_PRIVATE);
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

    private void displayAlert(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Are you sure ?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    User.setSection(section);
                    storeUserOnDB(User.getUid(), section);
                    storeUserOnDisk(User.getUid(), section);
                    currentActivity.goToMainActivity();
                })
                .setNegativeButton("No", null)
                .setMessage("Once selected, the selected section will be permanent ! Do you want to proceed ?")
                .show();
    }

    private void storeUserOnDB(String uid, Section section) {
        FirebaseBackend backend = new FirebaseBackend();
        backend.initUserInDB(uid, section);
    }

    private void storeUserOnDisk(String uid, Section section) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("UID", uid);
        editor.putString("Section", section.toString());
        editor.apply();
    }

}