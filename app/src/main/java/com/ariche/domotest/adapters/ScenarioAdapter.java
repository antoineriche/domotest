package com.ariche.domotest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.ariche.domotest.R;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.client.JeedomClient;
import com.ariche.domotest.jeedom.model.Scenario;

import java.util.List;

import static com.ariche.domotest.utils.LogUtils.logError;

public final class ScenarioAdapter extends BaseAdapter {

    private List<Scenario> mScenarioList;
    private LayoutInflater layoutInflater;
    private JeedomClient mJeedomClient;
    private ViewHolder holder;
    private Listener listener;

    public ScenarioAdapter(Context context,
                           List<Scenario> scenarioList,
                           Listener listener) {
        this.mScenarioList = scenarioList;
        this.mJeedomClient = new JeedomClient(context);
        this. layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return mScenarioList.size();
    }

    @Override
    public Scenario getItem(int i) {
        return mScenarioList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(mScenarioList.get(i).getId());
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            // ListItemScenarioBinding binding = ListItemScenarioBinding.inflate(layoutInflater, viewGroup, false);
            view = layoutInflater.inflate(R.layout.list_item_scenario, null);
            holder = new ViewHolder();
            holder.textViewName = view.findViewById(R.id.scenario_name);
            holder.textViewLastLaunch = view.findViewById(R.id.scenario_last_launch);
            holder.switchState = view.findViewById(R.id.scenario_state);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Scenario scenario = this.mScenarioList.get(position);
        holder.textViewName.setText(scenario.getName());
        holder.textViewLastLaunch.setText(scenario.getLastLaunch());
        holder.switchState.setClickable(false);
        holder.switchState.setOnClickListener(view1 -> toggleScenario(scenario));
        holder.switchState.setChecked(scenario.isActivated());

        return view;
    }

    static class ViewHolder {
        TextView textViewName;
        TextView textViewLastLaunch;
        Switch switchState;
    }

    private void toggleScenario(final Scenario scenario) {
        new Thread(() -> {
            try {
                mJeedomClient.toggleScenario(scenario.getId(), !scenario.isActivated());
                this.listener.needUpdate();
            } catch (HttpClientException e) {
                logError("Can not switch state for scenario: " + e.getMessage(), e);
            }
        }).start();
    }

    public interface Listener {
        void needUpdate();
    }
}
