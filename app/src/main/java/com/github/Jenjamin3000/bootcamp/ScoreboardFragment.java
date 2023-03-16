package com.github.Jenjamin3000.bootcamp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Jenjamin3000.bootcamp.scoreboard.ScoreHandler;
import com.github.Jenjamin3000.bootcamp.scoreboard.ScoreListFragment;
import com.github.Jenjamin3000.bootcamp.scoreboard.ScoreRecyclerViewAdapter;
import com.github.Jenjamin3000.bootcamp.scoreboard.placeholder.PlaceholderScoreHandler;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoreboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreboardFragment extends Fragment {

    private ScoreHandler handler;

    public ScoreboardFragment() {
        // Required empty public constructor
    }

    public ScoreboardFragment(ScoreHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScoreboardFragment.
     */
    public static ScoreboardFragment newInstance() {
        ScoreboardFragment fragment = new ScoreboardFragment();
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
        View view =  inflater.inflate(R.layout.fragment_scoreboard, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.scoreboard_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new ScoreRecyclerViewAdapter(handler.getScores()));

        return view;
    }
}