package com.example.nettools.ui.ping;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the ping fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}