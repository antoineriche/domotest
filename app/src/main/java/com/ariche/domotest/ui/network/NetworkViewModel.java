package com.ariche.domotest.ui.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NetworkViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NetworkViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is network fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}