package com.ariche.domotest.ui.lights;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;

@Getter
public class LightsViewModel extends ViewModel {

    private MutableLiveData<String> mButtonToggleText;

    public LightsViewModel() {
        mButtonToggleText = new MutableLiveData<>();
    }

}