package com.ariche.domotest.ui.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

import static com.ariche.domotest.utils.LogUtils.logInfo;

public class NetworkSniffTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> mContextRef;

    public NetworkSniffTask(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        logInfo("Let's sniff the network");

        try {
            Context context = mContextRef.get();

            if (context != null) {

                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                WifiInfo connectionInfo = wm.getConnectionInfo();
                int ipAddress = connectionInfo.getIpAddress();
                String ipString = Formatter.formatIpAddress(ipAddress);


                logInfo("activeNetwork: " + String.valueOf(activeNetwork));
                logInfo("ipString: " + String.valueOf(ipString));

                String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                logInfo("prefix: " + prefix);

                for (int i = 0; i < 255; i++) {
                    String testIp = prefix + String.valueOf(i);

                    InetAddress address = InetAddress.getByName(testIp);
                    boolean reachable = address.isReachable(200);
                    String hostName = address.getCanonicalHostName();

                    if (reachable) {
                        logInfo("Host: " + String.valueOf(hostName) + "(" + String.valueOf(testIp) + ") is reachable!");
                    } else {
                        logInfo("Not reachable: " + testIp);
                    }
                }
            }
        } catch (Throwable t) {
            logInfo("Well that's not good.", t);
        }

        return null;
    }
}