package com.ariche.domotest.ui.lights;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ariche.domotest.databinding.FragmentLightsBinding;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.client.JeedomClient;

import static com.ariche.domotest.utils.LogUtils.logError;

public class LightsFragment extends Fragment {

    private LightsViewModel lightsViewModel;
    private FragmentLightsBinding binding;
    private JeedomClient jeedomClient;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        lightsViewModel =
                new ViewModelProvider(this).get(LightsViewModel.class);

        binding = FragmentLightsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.switchLightsLivingRoom.setClickable(false);
        binding.switchLightsLivingRoom.setEnabled(false);
        binding.switchLightsLivingRoom.setOnClickListener(view -> toggleLight());

        initLightStatus();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        jeedomClient = new JeedomClient(getContext());
    }

    private boolean isLightOn() {
        try {
            return jeedomClient.isLightOn();
        } catch (HttpClientException e) {
            logError("Can not get light status: " + e.getMessage(), e);
            return false;
        }
    }

    private void initLightStatus() {
        new Thread(() -> {
            final boolean isOn = isLightOn();
            toggleSwitch(isOn);
            getActivity().runOnUiThread(() -> binding.switchLightsLivingRoom.setEnabled(true));
        }).start();
    }

    private void toggleLight() {
        new Thread(() -> {
            try {
                final boolean isOn = jeedomClient.isLightOn();
                jeedomClient.toggleLight(!isOn);
                toggleSwitch(!isOn);
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not toggle light: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void toggleSwitch(final boolean isOn) {
        getActivity().runOnUiThread(() -> binding.switchLightsLivingRoom.setChecked(isOn));
    }

}