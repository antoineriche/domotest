package com.ariche.domotest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ariche.domotest.databinding.ActivityMainBinding;
import com.ariche.domotest.freebox.client.FreeBoxClient;
import com.ariche.domotest.http.error.HttpClientException;
import com.ariche.domotest.jeedom.client.JeedomClient;
import com.ariche.domotest.utils.PreferenceHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityMainBinding binding;
    private BroadcastReceiver mBroadcastReceiver;
    private FreeBoxClient mFreeBoxClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        PreferenceHelper.registerSharedPreferencesListener(this, this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard,
                R.id.navigation_notifications, R.id.navigation_lights, R.id.navigation_network)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        registerBroadcastReceiver();
        mFreeBoxClient = new FreeBoxClient(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        final boolean wifiUsed = PreferenceHelper.getBooleanPreference(PreferenceHelper.USE_WIFI, this);
        menu.findItem(R.id.wifi_enabled).setVisible(wifiUsed);
        menu.findItem(R.id.wifi_disabled).setVisible(!wifiUsed);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBroadcastReceiver != null) {
            unregisterReceiver(this.mBroadcastReceiver);
        }
        PreferenceHelper.unregisterSharedPreferencesListener(this, this);
    }

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final NetworkInfo netInfo = intent.getParcelableExtra (WifiManager.EXTRA_NETWORK_INFO);
                if (ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
                    boolean useLocalAddress = false;
                    if (netInfo.isConnected()) {
                        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        final WifiInfo info = wifiManager.getConnectionInfo();
                        useLocalAddress = info.getSSID()
                                .replaceAll("\"", "")
                                .equalsIgnoreCase(FreeBoxClient.getFreeBoxSSID(context));
                    }

                    if (useLocalAddress) {
                        discoverLocalJeedomAddress();
                    } else {
                        final String jeedomBaseURL = JeedomClient.buildPublicURL(context);
                        PreferenceHelper.storeUseWiFi(false, context);
                        PreferenceHelper.storeRaspberryAddress(jeedomBaseURL, context);
                    }
                }
            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        registerReceiver(mBroadcastReceiver, filter);
    }

    private void discoverLocalJeedomAddress() {
        new Thread(() -> {
            String jeedomAddress;
            try {
                jeedomAddress = mFreeBoxClient.getLocalJeedomAddress()
                        .orElse(JeedomClient.buildPublicURL(this));
            } catch (HttpClientException e) {
                runOnUiThread(() -> Toast.makeText(getBaseContext(), "Can not get local Jeedom address", Toast.LENGTH_SHORT));
                jeedomAddress =  JeedomClient.buildPublicURL(this);
            }

            final boolean isWiFiUsed = jeedomAddress.startsWith("192.168");
            PreferenceHelper.storeUseWiFi(isWiFiUsed, this);
            PreferenceHelper.storeRaspberryAddress(jeedomAddress, this);
        }).start();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean wifiUsed = PreferenceHelper.getBooleanPreference(PreferenceHelper.USE_WIFI, this);
        menu.findItem(R.id.wifi_enabled).setVisible(wifiUsed);
        menu.findItem(R.id.wifi_disabled).setVisible(!wifiUsed);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (PreferenceHelper.USE_WIFI.equalsIgnoreCase(s)) {
            invalidateOptionsMenu();
        }
    }
}