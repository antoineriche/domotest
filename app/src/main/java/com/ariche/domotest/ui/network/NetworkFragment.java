package com.ariche.domotest.ui.network;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ariche.domotest.databinding.FragmentNetworkBinding;
import com.ariche.domotest.freebox.model.FreeBoxApiVersion;
import com.ariche.domotest.freebox.client.FreeBoxClient;
import com.ariche.domotest.freebox.client.FreeBoxResponse;
import com.ariche.domotest.freebox.model.FreeBoxDevice;
import com.ariche.domotest.freebox.model.InterfaceOutput;
import com.ariche.domotest.freebox.model.LoginStatusOutput;
import com.ariche.domotest.freebox.model.OpenSessionOutput;
import com.ariche.domotest.jeedom.client.JeedomClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ariche.domotest.utils.LogUtils.logError;
import static com.ariche.domotest.utils.LogUtils.logInfo;

public class NetworkFragment extends Fragment {

    private NetworkViewModel networkViewModel;
    private FragmentNetworkBinding binding;

    private FreeBoxClient freeBoxClient;
    private JeedomClient jeedomClient;

    private boolean lightOn = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        networkViewModel =
                new ViewModelProvider(this).get(NetworkViewModel.class);

        binding = FragmentNetworkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.cardView.setVisibility(View.GONE);
        // networkViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.buttonAppVersion.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    FreeBoxApiVersion version = freeBoxClient.getApiVersion();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), version.toString(), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        binding.buttonLoginStatus.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    FreeBoxResponse<LoginStatusOutput> status = freeBoxClient.getLoginStatus();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), status.getResult().getStatus(), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        binding.buttonOpenSession.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    FreeBoxResponse<OpenSessionOutput> session = freeBoxClient.openSession();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), session.getResult().getSessionToken(), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        binding.buttonListInterfaces.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    FreeBoxResponse<InterfaceOutput[]> interfaces = freeBoxClient.listInterfaces();
                    Arrays.stream(interfaces.getResult()).forEach(fInterface ->
                            logInfo(String.format("%s : %d", fInterface.getName(), fInterface.getHostCount())));
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), interfaces.getResult().length + " interface(s) found", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        binding.buttonListDevices.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    FreeBoxResponse<FreeBoxDevice[]> devices = freeBoxClient.listConnectedDevices("pub");
                    if (devices.isSuccess()) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), devices.getResult().length + " device(s) found", Toast.LENGTH_SHORT).show();
                            Arrays.stream(devices.getResult())
                                    .filter(device -> device.getName().equalsIgnoreCase("pi"))
                                    .findFirst()
                                    .ifPresent(this::handlePiDeviceDiscovered);
                        });
                    }
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        binding.buttonJeedomWeather.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    final String weather = jeedomClient.getWeather();
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), weather, Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });


        binding.buttonJeedomLight.setOnClickListener(view -> {
            new Thread(() -> {
                try {
                    final String weather = jeedomClient.toggleLight(!lightOn);
                    lightOn = !lightOn;
                } catch (Exception e) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            }).start();
        });

        return root ;
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
    }

    private void handlePiDeviceDiscovered(final FreeBoxDevice device) {
        binding.cardView.setVisibility(View.VISIBLE);
        jeedomClient.setPiAddress(device.getAddress());
        final String s = String.format(Locale.FRANCE,
                "Device: %s\nVendor: %s\nAddress: %s\nLast activity: %s\nLast time reachable: %s",
                device.getName(),
                device.getVendorName(), device.getAddress(),
                new Date(device.getLastActivity() * 1_000),
                new Date(device.getLastTimeReachable() * 1_000));
        logInfo(s);
        binding.textviewPi.setText(s);
    }
}