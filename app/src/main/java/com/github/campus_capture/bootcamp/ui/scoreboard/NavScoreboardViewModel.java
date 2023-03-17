package com.github.campus_capture.bootcamp.ui.scoreboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NavScoreboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NavScoreboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}