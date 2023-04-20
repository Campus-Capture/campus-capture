package com.github.campus_capture.bootcamp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.activities.MainActivity;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.FireDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private TextView selectedSection;
    private Spinner sectionSpinner;
    private TextView userName;
    private Button confirmButton;
    private Section section;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(new Bundle());
        return fragment;
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
        sectionSpinner = view.findViewById(R.id.profile_section_spinner);
        userName = view.findViewById(R.id.profile_name);
        confirmButton = view.findViewById(R.id.profile_confirm_button);
        selectedSection = view.findViewById(R.id.profile_section_selected);

        // Display the name of the player
        userName.setText(User.getName());

        // Only show section selection if not already registered
        if (!User.hasSelectedSection()) {
            showSelection(true);
            setSpinnerListener(sectionSpinner);
            setConfirmButtonListener(confirmButton);
        } else {
            showSelection(false);
        }

        return view;
    }

    private void setConfirmButtonListener(Button confirmButton) {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlert(v);
            }
        });
    }

    /**
     * Show the corresponding feature of the fragment based on if the selection as already
     * been made before.
     * @param b true to show selection, false to hide it.
     */
    private void showSelection(boolean b) {
        if(b){
            selectedSection.setVisibility(View.GONE);

            sectionSpinner.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.VISIBLE);
        } else {
            selectedSection.setText(User.getSection().toString());

            sectionSpinner.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);

            selectedSection.setVisibility(View.VISIBLE);
        }
    }

    private void setSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                section = Section.values()[(int) id];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void displayAlert(View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        User.setSection(section);

                        FireDatabase.initUser(section);

                        showSelection(false);
                    }
                })
                .setNegativeButton("No", null)
                .setMessage("Once selected, the selected section will be permanent ! Do you want to proceed ?")
                .show();
    }
}