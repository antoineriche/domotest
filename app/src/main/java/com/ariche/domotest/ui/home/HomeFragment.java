package com.ariche.domotest.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ariche.domotest.databinding.FragmentHomeBinding;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.client.JeedomClient;
import com.ariche.domotest.utils.PreferenceHelper;

import java.util.List;

public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private int count;
    private JeedomClient mJeedomClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final String pi = PreferenceHelper.getPreference(PreferenceHelper.PI_ADDRESS, getContext());
        binding.tvJeedomAddress.setText(pi);

        //binding.tvJeedomAddress;

        loadWeather();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        mJeedomClient = null;
        PreferenceHelper.unregisterSharedPreferencesListener(getContext(), this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        PreferenceHelper.registerSharedPreferencesListener(context, this);
        this.mJeedomClient = new JeedomClient(context);
    }

    private void loadWeather() {
        new Thread(() -> {
            try {
                final List<String> weathers = mJeedomClient.getWeather();
                getActivity().runOnUiThread(() -> {
                    binding.textviewWeatherCondition1.setText(weathers.get(0));
                    binding.textviewWeatherCondition2.setText(weathers.get(1));
                    binding.textviewWeatherCondition3.setText(weathers.get(2));
                    binding.textviewWeatherCondition4.setText(weathers.get(3));
                    binding.textviewWeatherTemperature.setText(weathers.get(4));
                    binding.textviewWeatherSunrise.setText(weathers.get(5));
                    binding.textviewWeatherSunset.setText(weathers.get(6));
                });
            } catch (HttpClientException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not get weather: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceHelper.PI_ADDRESS.equalsIgnoreCase(key)) {
            final String s0 = binding.tvJeedomAddress.getText() != null ?
                    binding.tvJeedomAddress.getText().toString() : "";
            final String s = s0.concat("\n")
                    .concat((count++) +  PreferenceHelper.getPreference(key, getContext()));
            binding.tvJeedomAddress.setText(s);
        }
    }
}