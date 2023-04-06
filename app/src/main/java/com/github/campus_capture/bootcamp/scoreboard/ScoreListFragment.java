package com.github.campus_capture.bootcamp.scoreboard;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;

/**
 * A fragment representing the score board
 */
public class ScoreListFragment extends Fragment {

    private static final int COLUMN_COUNT = 2;

    private BackendInterface backendInterface;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreListFragment() {
    }

    public ScoreListFragment(BackendInterface backend)
    {
        backendInterface = backend;
    }

    public static ScoreListFragment newInstance(BackendInterface backend) {
        ScoreListFragment fragment = new ScoreListFragment(backend);
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

            // TODO not sure how to refactor this correctly using the new future interface
            try {
                recyclerView.setAdapter(new ScoreRecyclerViewAdapter(backendInterface.getScores().get()));
            }catch(Exception e){}
        }
        return view;
    }
}