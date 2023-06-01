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
import com.github.campus_capture.bootcamp.authentication.Section;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.github.campus_capture.bootcamp.scoreboard.ScoreRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoreboardFragment} factory method to
 * create an instance of this fragment.
 */
public class ScoreboardFragment extends Fragment {

    private final List<ScoreItem> mockScores = new ArrayList<>();

    private BackendInterface backendInterface;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    public ScoreboardFragment(BackendInterface backend)
    {
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
        View view =  inflater.inflate(R.layout.fragment_scoreboard, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.scoreboard_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(MapsFragment.endOfTakeOver) {
            mockScores.add(new ScoreItem(Section.IN.name(), 3));

            mockScores.add(new ScoreItem(Section.AR.name(), 2));
        } else {

            mockScores.add(new ScoreItem(Section.AR.name(), 3));

            mockScores.add(new ScoreItem(Section.IN.name(), 2));
        }

        for (Section s: Section.values()) {
            if(!s.equals(Section.AR) && !s.equals(Section.IN) && !s.equals(Section.NONE)) {
                mockScores.add(new ScoreItem(s.name(), 2));
            }
        }

        recyclerView.setAdapter(new ScoreRecyclerViewAdapter(mockScores));

        return view;
    }
}