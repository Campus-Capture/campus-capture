package com.github.campus_capture.bootcamp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.FirebaseInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreRecyclerViewAdapter;

public class ScoreboardFragment extends Fragment {

    private FirebaseInterface firebaseInterface;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    public ScoreboardFragment(FirebaseInterface backend)
    {
        firebaseInterface = backend;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_scoreboard, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.scoreboard_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new ScoreRecyclerViewAdapter(firebaseInterface.getScores()));

        return view;
    }
}