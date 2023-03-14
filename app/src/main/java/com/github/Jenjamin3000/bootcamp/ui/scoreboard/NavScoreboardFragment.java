package com.github.Jenjamin3000.bootcamp.ui.scoreboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.Jenjamin3000.bootcamp.databinding.FragmentTestBinding;

public class NavScoreboardFragment extends Fragment {

    private FragmentTestBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NavScoreboardViewModel navMainViewModel =
                new ViewModelProvider(this).get(NavScoreboardViewModel.class);

        binding = FragmentTestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}