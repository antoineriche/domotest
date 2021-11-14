package com.ariche.domotest.ui.network;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;

@Getter
public class NetworkViewModel extends ViewModel {

    // FREEBOX
    private final MutableLiveData<String> mFreeBoxModelName;
    private final MutableLiveData<String> mFreeBoxDeviceName;
    private final MutableLiveData<String> mFreeBoxDeviceType;
    private final MutableLiveData<String> mFreeBoxLoginStatus;
    private final MutableLiveData<String> mFreeBoxInterfacesCount;
    private final MutableLiveData<String> mFreeBoxDevicesCount;

    // RASPBERRY
    private final MutableLiveData<String> mRaspberryName;
    private final MutableLiveData<String> mRaspberryAddress;
    private final MutableLiveData<String> mRaspberryLastActivity;


    public NetworkViewModel() {
        mFreeBoxModelName = new MutableLiveData<>();
        mFreeBoxDeviceName = new MutableLiveData<>();
        mFreeBoxDeviceType = new MutableLiveData<>();
        mFreeBoxLoginStatus = new MutableLiveData<>();
        mFreeBoxInterfacesCount = new MutableLiveData<>();
        mFreeBoxDevicesCount = new MutableLiveData<>();

        mRaspberryName = new MutableLiveData<>();
        mRaspberryAddress = new MutableLiveData<>();
        mRaspberryLastActivity = new MutableLiveData<>();
    }

}