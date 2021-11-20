package com.ariche.domotest.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ariche.domotest.adapters.ScenarioAdapter;
import com.ariche.domotest.databinding.FragmentDashboardBinding;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.client.JeedomClient;
import com.ariche.domotest.jeedom.model.Scenario;
import com.ariche.domotest.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import static com.ariche.domotest.utils.LogUtils.logInfo;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentDashboardBinding binding;
    private JeedomClient jeedomClient;

    private List<Scenario> mScenarios;
    private ScenarioAdapter mScenarioAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        PreferenceHelper.registerSharedPreferencesListener(getContext(), jeedomClient);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        this.mScenarios = new ArrayList<>();
        this.mScenarioAdapter = new ScenarioAdapter(getContext(), mScenarios, this::getScenario);
        binding.listViewScenario.setAdapter(mScenarioAdapter);

        getScenario();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        PreferenceHelper.unregisterSharedPreferencesListener(getContext(), jeedomClient);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        jeedomClient = new JeedomClient(getContext());
    }

    public void getScenario() {
        new Thread(() -> {
            try {
                final List<Scenario> list = this.jeedomClient.listScenarios();
                this.mScenarios.clear();
                this.mScenarios.addAll(list);
                logInfo("List: " + list);
                getActivity().runOnUiThread(() -> mScenarioAdapter.notifyDataSetChanged());
            } catch (HttpClientException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Can not get scenario: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();

    }

}