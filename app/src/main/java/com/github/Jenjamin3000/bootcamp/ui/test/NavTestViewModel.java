package com.github.Jenjamin3000.bootcamp.ui.test;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NavTestViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public NavTestViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}