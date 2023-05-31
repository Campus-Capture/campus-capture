package com.github.campus_capture.bootcamp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.scoreboard.ScoreItem;
import com.github.campus_capture.bootcamp.scoreboard.ScoreRecyclerViewAdapter;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class ScoreboardFragment extends Fragment {

    private CountDownTimer timer;

    private List<ScoreItem> items;

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

        items = new ArrayList<>();
        items.add(new ScoreItem("IN", 0));
        items.add(new ScoreItem("SC", 0));
        items.add(new ScoreItem("MT", 0));
        items.add(new ScoreItem("GC", 0));
        items.add(new ScoreItem("SV", 0));
        items.add(new ScoreItem("AR", 0));
        items.add(new ScoreItem("CGC", 0));
        items.add(new ScoreItem("MA", 0));
        items.add(new ScoreItem("EL", 0));
        items.add(new ScoreItem("MX", 0));
        items.add(new ScoreItem("GM", 0));
        items.add(new ScoreItem("SIE", 0));
        Collections.sort(items);

        recyclerView.setAdapter(new ScoreRecyclerViewAdapter(items));

        timer = new CountDownTimer(100, 200) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                for(ScoreItem s : items)
                {
                    s.addScore(ThreadLocalRandom.current().nextInt(1, 10));
                }
                Collections.sort(items);
                recyclerView.setAdapter(new ScoreRecyclerViewAdapter(items));
                timer.start();
            }
        };
        timer.start();

        return view;
    }
}