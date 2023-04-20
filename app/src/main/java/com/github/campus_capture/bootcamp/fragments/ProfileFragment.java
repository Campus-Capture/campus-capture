package com.github.campus_capture.bootcamp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.github.campus_capture.bootcamp.R;

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

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*

        Section section = switchForButtonSection(mRadioGroup);

        Toast.makeText(AuthenticationActivity.this, section.toString(), Toast.LENGTH_SHORT).show();
        User.setSection(section);

        FireDatabase.initUser(section);

         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button invite_button = view.findViewById(R.id.invite_button);
        invite_button.setOnClickListener(invite_listener);

        return view;
    }
}