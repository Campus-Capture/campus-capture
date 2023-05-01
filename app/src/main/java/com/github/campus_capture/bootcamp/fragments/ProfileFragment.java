package com.github.campus_capture.bootcamp.fragments;

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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.authentication.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    // The listener to open the sharing intent once the invite button is pressed
    private final View.OnClickListener invite_listener = v -> {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String message = getString(R.string.invitation_text) + getString(R.string.invitation_link);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        // Use a chooser for better visuals (the default send intent is ugly)
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    };

    private TextView selectedSection;
    private Spinner sectionSpinner;
    private TextView userName;
    private Button confirmButton;
    private Section section = Section.NONE;
    private DatabaseReference userRef;

    public ProfileFragment() {
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Retriev info of the user from database
        AppContext context = AppContext.getAppContext();
        FirebaseDatabase db = context.getFirebaseDB();

        userRef = db.getReference(User.getUid());
        DatabaseReference sectionRef = userRef.child("Section");

        if (Objects.isNull(sectionRef.)) {
            section = Section.NONE;
        } else {
            section = Section.valueOf(sectionRef.get());
        }

        Button invite_button = view.findViewById(R.id.invite_button);
        invite_button.setOnClickListener(invite_listener);

        // Retrieve the views from the layout
        sectionSpinner = view.findViewById(R.id.profile_section_spinner);
        userName = view.findViewById(R.id.profile_name);
        confirmButton = view.findViewById(R.id.profile_confirm_button);
        selectedSection = view.findViewById(R.id.profile_section_selected);

        // Display the name of the player
        String name = userRef.child("email").toString();
        userName.setText(name);

        // Only show section selection if not already registered
        if (section == Section.NONE) {
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

                        // Finaly we can set the section in database and in User
                        userRef.child("Section").setValue(section.toString());
                        User.setSection(section);

                        showSelection(false);
                    }
                })
                .setNegativeButton("No", null)
                .setMessage("Once selected, the selected section will be permanent ! Do you want to proceed ?")
                .show();
    }
}