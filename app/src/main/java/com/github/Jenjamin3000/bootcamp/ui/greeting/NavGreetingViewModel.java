package com.github.Jenjamin3000.bootcamp.ui.greeting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NavGreetingViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NavGreetingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is main fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}