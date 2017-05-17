package com.dvlchm.fsa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

    public interface NetworkListener {
        void onNetworkAvailable();
        void onNetworkUnavailable();
    }

    private NetworkListener networkListener;

    public NetworkStateReceiver(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                Log.i("app", "Network " + ni.getTypeName() + " connected");
                networkListener.onNetworkAvailable();
            }
        }
        if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            Log.d("app", "There's no network connectivity");
            networkListener.onNetworkUnavailable();
        }
    }
}