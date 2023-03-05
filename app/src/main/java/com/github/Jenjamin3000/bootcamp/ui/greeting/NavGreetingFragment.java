package com.github.Jenjamin3000.bootcamp.ui.greeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.Jenjamin3000.bootcamp.databinding.FragmentGreetingBinding;

public class NavGreetingFragment extends Fragment {

    private FragmentGreetingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NavGreetingViewModel navMainViewModel =
                new ViewModelProvider(this).get(NavGreetingViewModel.class);

        binding = FragmentGreetingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGreeting;
        navMainViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}