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
import com.github.campus_capture.bootcamp.shop.PowerUpRecyclerViewAdapter;

import java.util.Arrays;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotePowerUpFragment extends Fragment {

    private final BackendInterface backendInterface;

    private int userMoney;


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

        backendInterface.getMoney().thenAccept((money) -> {
            userMoney = money;
            ((TextView) view.findViewById(R.id.power_up_money)).setText(String.format(Locale.ENGLISH, "Money: %d", money));

        }).exceptionally(e->{
            Log.e("VotePowerUpFragment", "Error occurred when fetching money: "+e);
            return null;
        });

        RecyclerView recyclerView = view.findViewById(R.id.power_up_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        backendInterface.getPowerUps().thenAccept((powerUpList) -> recyclerView.setAdapter(new PowerUpRecyclerViewAdapter(powerUpList, userMoney, backendInterface, (TextView) view.findViewById(R.id.power_up_money)))).exceptionally(e -> {
            Log.e("VotePowerUpFragmennt", "Error ocurred when fetching power ups: "+e);
            return null;
        });

        return view;
    }
}