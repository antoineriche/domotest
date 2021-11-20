package com.ariche.domotest.ui.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ariche.domotest.R;
import com.ariche.domotest.databinding.FragmentNetworkBinding;
import com.ariche.domotest.freebox.client.FreeBoxClient;
import com.ariche.domotest.freebox.client.FreeBoxResponse;
import com.ariche.domotest.freebox.model.FreeBoxApiVersion;
import com.ariche.domotest.freebox.model.FreeBoxDevice;
import com.ariche.domotest.freebox.model.LoginStatusOutput;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.client.JeedomClient;
import com.ariche.domotest.utils.PreferenceHelper;
import com.ariche.domotest.utils.PropertyUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class NetworkFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private NetworkViewModel networkViewModel;
    private FragmentNetworkBinding binding;

    private FreeBoxClient freeBoxClient;
    private JeedomClient jeedomClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        networkViewModel =
                new ViewModelProvider(this).get(NetworkViewModel.class);

        PreferenceHelper.registerSharedPreferencesListener(getContext(), this, jeedomClient);
        binding = FragmentNetworkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initView(networkViewModel, binding);
        binding.tvJeedomAddress.setText(PreferenceHelper.getPreference(PreferenceHelper.PI_ADDRESS, getContext()));
        binding.switchUseWifi.setClickable(false);
        binding.switchUseWifi.setChecked(PreferenceHelper.getBooleanPreference(PreferenceHelper.USE_WIFI, getContext()));
        // TODO: uncomment loadFreeBoxInfo();
        // TODO: uncomment discoverRaspberryPi();

        binding.buttonDiscoverPi.setOnClickListener(view -> discoverRaspberryPi());

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        freeBoxClient = new FreeBoxClient(getContext());
        jeedomClient = new JeedomClient(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        PreferenceHelper.unregisterSharedPreferencesListener(getContext(), this, jeedomClient);
    }

    private void handlePiDeviceDiscovered(final FreeBoxDevice device) {
        final String sDate = new SimpleDateFormat("yyyy/MM/dd HH:mm.ss", Locale.FRANCE)
                .format(device.getLastActivity() * 1_000);

        binding.textviewRaspberryName.setText(device.getName());
        binding.textviewRaspberryAddress.setText(device.getAddress());
        binding.textviewRaspberryLastActivity.setText(sDate);
        jeedomClient.setPiAddress(device.getAddress());
    }

    private void loadFreeBoxInfo() {
        new Thread(() -> {
            try {
                final FreeBoxApiVersion version = freeBoxClient.getApiVersion();
                binding.textviewModelName.setText(version.getBoxModelName());
                binding.textviewDeviceName.setText(version.getDeviceName());
                binding.textviewDeviceType.setText(version.getDeviceType());
            } catch (HttpClientException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not get FreeBox info", Toast.LENGTH_SHORT).show());
            }

            try {
                final FreeBoxResponse<LoginStatusOutput> status = freeBoxClient.getLoginStatus();
                binding.textviewLoginStatus.setText(status.getResult().getStatus());
            } catch (HttpClientException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void discoverRaspberryPi() {
        new Thread(() -> {
            try {
                freeBoxClient.openSession();
            } catch (HttpClientException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not open session: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            try {
                final String piName = PropertyUtils.loadFreeBoxProperties(getContext()).getProperty("freebox.raspberry.host");
                final FreeBoxResponse<FreeBoxDevice[]> devices = freeBoxClient.listPublicInterfaceConnectedDevices();

                final String devicesFound = getResources().getQuantityString(R.plurals.network_devices_found, devices.getResult().length, devices.getResult().length);
                binding.textviewConnectedDevices.setText(devicesFound);
                if (devices.isSuccess()) {
                    Arrays.stream(devices.getResult())
                            .filter(device -> device.getName().equalsIgnoreCase(piName))
                            .findFirst()
                            .ifPresent(this::handlePiDeviceDiscovered);
                }
            } catch (HttpClientException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not discover pi address: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

        }).start();
    }


    private void initView(final NetworkViewModel networkViewModel,
                          final FragmentNetworkBinding binding) {
        // FreeBox
        networkViewModel.getMFreeBoxDeviceName().observe(getViewLifecycleOwner(), binding.textviewDeviceName::setText);
        networkViewModel.getMFreeBoxModelName().observe(getViewLifecycleOwner(), binding.textviewModelName::setText);
        networkViewModel.getMFreeBoxDeviceType().observe(getViewLifecycleOwner(), binding.textviewDeviceType::setText);
        networkViewModel.getMFreeBoxLoginStatus().observe(getViewLifecycleOwner(), binding.textviewLoginStatus::setText);
        networkViewModel.getMFreeBoxInterfacesCount().observe(getViewLifecycleOwner(), binding.textviewInterfacesCount::setText);
        networkViewModel.getMFreeBoxDevicesCount().observe(getViewLifecycleOwner(), binding.textviewInterfacesCount::setText);

        // Raspberry
        networkViewModel.getMRaspberryName().observe(getViewLifecycleOwner(), binding.textviewRaspberryName::setText);
        networkViewModel.getMRaspberryAddress().observe(getViewLifecycleOwner(), binding.textviewRaspberryAddress::setText);
        networkViewModel.getMRaspberryLastActivity().observe(getViewLifecycleOwner(), binding.textviewRaspberryLastActivity::setText);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceHelper.PI_ADDRESS.equalsIgnoreCase(key)) {
            binding.tvJeedomAddress.setText(PreferenceHelper.getPreference(key, getContext()));
        }

        if (PreferenceHelper.USE_WIFI.equalsIgnoreCase(key)) {
            binding.switchUseWifi.setChecked(PreferenceHelper.getBooleanPreference(key, getContext()));
        }
    }
}