package com.example.nettools.ui.traceroute;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TracerouteViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TracerouteViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}