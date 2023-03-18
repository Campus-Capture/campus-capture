package com.github.campus_capture.bootcamp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.campus_capture.bootcamp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GreetingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GreetingFragment extends Fragment {

    public GreetingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GreetingFragment.
     */
    public static GreetingFragment newInstance() {
        GreetingFragment fragment = new GreetingFragment();
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
        return inflater.inflate(R.layout.fragment_greeting, container, false);
    }
}