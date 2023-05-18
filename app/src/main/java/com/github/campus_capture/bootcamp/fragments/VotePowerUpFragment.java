package com.github.campus_capture.bootcamp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreRecyclerViewAdapter;
import com.github.campus_capture.bootcamp.shop.PowerUpRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotePowerUpFragment extends Fragment {

    private BackendInterface backendInterface;


    public VotePowerUpFragment(BackendInterface backend) {
        backendInterface = backend;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vote_power_up, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.power_up_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        backendInterface.getPowerUps().thenAccept((powerUpList) ->{

            recyclerView.setAdapter(new PowerUpRecyclerViewAdapter(powerUpList));

        }).exceptionally( e -> {
            // TODO handle errors better ?
            Log.e("VotePowerUpFragmennt", "Error ocurred when fetching power ups");
            return null;
        });

        return view;
    }
}