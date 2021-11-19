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

    private final static int BRIGHTNESS_THRESHOLD = 20;

    private LightsViewModel lightsViewModel;
    private FragmentLightsBinding binding;
    private JeedomClient jeedomClient;

    private int currentBrightness;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        lightsViewModel =
                new ViewModelProvider(this).get(LightsViewModel.class);

        binding = FragmentLightsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.switchLightsLivingRoom.setClickable(false);
        binding.switchLightsLivingRoom.setEnabled(false);
        binding.switchLightsLivingRoom.setOnClickListener(view -> toggleLight(Light.LIVING));

        binding.switchLightsPath.setClickable(false);
        binding.switchLightsPath.setEnabled(false);
        binding.switchLightsPath.setOnClickListener(view -> toggleLight(Light.LIGHT_PATH));

        binding.switchCeilingLight.setClickable(false);
        binding.switchCeilingLight.setEnabled(false);
        binding.switchCeilingLight.setOnClickListener(view -> toggleLight(Light.LIVING_CEILING));

        binding.btnBrightnessPlus.setOnClickListener(view -> updateCeilingBrightness(true));
        binding.btnBrightnessMinus.setOnClickListener(view -> updateCeilingBrightness(false));

        binding.btnCeilingBlue.setOnClickListener(view -> updateCeilingColor("#0000FF"));
        binding.btnCeilingGreen.setOnClickListener(view -> updateCeilingColor("#00FF00"));
        binding.btnCeilingRed.setOnClickListener(view -> updateCeilingColor("#FF0000"));
        binding.btnCeilingWhite.setOnClickListener(view -> updateCeilingColor("#FFFFFF"));
        binding.btnCeilingReset.setOnClickListener(view -> updateCeilingColor("#FFF000"));

        initLightStatus();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        jeedomClient = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        jeedomClient = new JeedomClient(getContext());
    }

    private boolean getLightStatus(final Light light) {
        try {
            return jeedomClient.isLightOn(light);
        } catch (HttpClientException e) {
            logError("Can not get light '"+light+"'status: " + e.getMessage(), e);
            return false;
        }
    }

    private void updateCeilingBrightness(final boolean more) {
        new Thread(() -> {
            final int offset = BRIGHTNESS_THRESHOLD * 255 / 100;
            final int targetValue = more ? currentBrightness + offset : currentBrightness - offset;
            try {
                final int brightness = jeedomClient.updateCeilingBrightnessValue(targetValue);
                getActivity().runOnUiThread(() -> binding.tvBrightnessValue.setText(brightness + ""));
                currentBrightness = brightness;
            } catch (HttpClientException e) {
                logError("Can not update ceiling brightness", e);
            }
        }).start();
    }

    private void updateCeilingColor(final String hexColor) {
        new Thread(() -> {
            try {
                jeedomClient.updateCeilingColor(hexColor);
            } catch (HttpClientException e) {
                logError("Can not update ceiling brightness", e);
            }
        }).start();
    }

    private void initLightStatus() {
        new Thread(() -> {
            boolean isOn;

            isOn = getLightStatus(Light.LIVING);
            toggleSwitch(isOn, Light.LIVING);
            getActivity().runOnUiThread(() -> binding.switchLightsLivingRoom.setEnabled(true));

            isOn = getLightStatus(Light.LIGHT_PATH);
            toggleSwitch(isOn, Light.LIGHT_PATH);
            getActivity().runOnUiThread(() -> binding.switchLightsPath.setEnabled(true));

            isOn = getLightStatus(Light.LIVING_CEILING);
            toggleSwitch(isOn, Light.LIVING_CEILING);
            getActivity().runOnUiThread(() -> binding.switchCeilingLight.setEnabled(true));

            try {
                currentBrightness = jeedomClient.getCeilingBrightnessValue();
                final String brightnessValue = currentBrightness + "";
                getActivity().runOnUiThread(() -> binding.tvBrightnessValue.setText(brightnessValue));
            } catch (HttpClientException e) {
                logError("can not get brightness from ceiling light", e);
            }

        }).start();
    }

    private void toggleLight(final Light light) {
        new Thread(() -> {
            try {
                final boolean isOn = jeedomClient.isLightOn(light);
                jeedomClient.toggleLight(!isOn, light);
                toggleSwitch(!isOn, light);
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not toggle light: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void toggleSwitch(final boolean isOn,
                              final Light light) {
        getActivity().runOnUiThread(() -> {
            switch (light) {
                case LIVING:
                    binding.switchLightsLivingRoom.setChecked(isOn);
                    break;
                case LIGHT_PATH:
                    binding.switchLightsPath.setChecked(isOn);
                    break;
                case LIVING_CEILING:
                    binding.switchCeilingLight.setChecked(isOn);
                    break;
            }
        });
    }

    public enum Light {
        LIVING,
        LIVING_CEILING,
        LIGHT_PATH
    }
}