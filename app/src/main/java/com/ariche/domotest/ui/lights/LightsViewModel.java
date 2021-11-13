package com.ariche.domotest.ui.lights;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LightsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LightsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is lights fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}