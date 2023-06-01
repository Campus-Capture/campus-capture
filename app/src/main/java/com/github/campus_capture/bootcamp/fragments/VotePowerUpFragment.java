package com.github.campus_capture.bootcamp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.shop.PowerUp;
import com.github.campus_capture.bootcamp.shop.PowerUpRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotePowerUpFragment extends Fragment {

    private final BackendInterface backendInterface;

    private int userMoney;

    public static int mockMoney = 20;

    public static List<PowerUp> mockPowerUps;


    public VotePowerUpFragment(BackendInterface backend) {
        backendInterface = backend;
        mockPowerUps = new ArrayList<>();
        mockPowerUps.add(new PowerUp("Double attacks", 20, 30));
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


        ((TextView) view.findViewById(R.id.power_up_money)).setText(String.format(Locale.ENGLISH, "Money: %d", mockMoney));



        RecyclerView recyclerView = view.findViewById(R.id.power_up_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new PowerUpRecyclerViewAdapter(mockPowerUps, mockMoney, backendInterface, (TextView) view.findViewById(R.id.power_up_money)));

        return view;
    }
}