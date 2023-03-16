package com.github.Jenjamin3000.bootcamp.scoreboard;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Jenjamin3000.bootcamp.R;

/**
 * A fragment representing the score board
 */
public class ScoreListFragment extends Fragment {

    private static final int COLUMN_COUNT = 2;

    private ScoreHandler handler;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreListFragment() {
    }

    public ScoreListFragment(ScoreHandler handler)
    {
        this.handler = handler;
    }

    public static ScoreListFragment newInstance(ScoreHandler handler) {
        ScoreListFragment fragment = new ScoreListFragment(handler);
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
        View view = inflater.inflate(R.layout.fragment_score_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, COLUMN_COUNT));

            recyclerView.setAdapter(new ScoreRecyclerViewAdapter(handler.getScores()));
        }
        return view;
    }
}