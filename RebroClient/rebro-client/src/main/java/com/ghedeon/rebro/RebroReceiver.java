package com.ghedeon.rebro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

public class RebroReceiver extends BroadcastReceiver {

    private static final String TAG = RebroReceiver.class.getSimpleName();

    private static final String SERVER_IP = "SERVER_IP";
    private static final String DEVICE_NAME = "DEVICE_NAME";

    /**
     * Action — com.vv.rebro.action.CONNECT
     */
    @Override
    public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
        final String serverIp = intent.getStringExtra(SERVER_IP);
        final String deviceName = intent.getStringExtra(DEVICE_NAME);
        Log.d(TAG, "intent received from server " + serverIp + " for " + deviceName);

        RebroService.start(context, serverIp, deviceName);
    }
}
