package com.github.Jenjamin3000.bootcamp.ui.rules;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.Jenjamin3000.bootcamp.R;

public class NavRulesFragment extends Fragment {

    private NavRulesViewModel mViewModel;

    public static NavRulesFragment newInstance() {
        return new NavRulesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_rules, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(NavRulesViewModel.class);
        // TODO: Use the ViewModel
    }

}