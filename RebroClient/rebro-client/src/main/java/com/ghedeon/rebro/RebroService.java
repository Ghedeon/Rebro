package com.ghedeon.rebro;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ghedeon.rebro.WsClient.OnSocketClosedListener;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

public class RebroService extends Service {

    private static final String ACTION_CONNECT = "ACTION_CONNECT";
    private static final String SERVER_IP = "SERVER_IP";
    private static final String DEVICE_NAME = "DEVICE_NAME";

    private RebroServiceThread serviceThread;

    public static void start(@NonNull final Context context, @NonNull final String serverIp, @NonNull final String deviceName) {
        final Intent intent = new Intent(context, RebroService.class);
        intent.setAction(ACTION_CONNECT);
        intent.putExtra(SERVER_IP, serverIp);
        intent.putExtra(DEVICE_NAME, deviceName);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        serviceThread = new RebroServiceThread();
        serviceThread.start();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final String action = intent.getAction();
        if (ACTION_CONNECT.equals(action)) {
            final String serverIp = intent.getStringExtra(SERVER_IP);
            final String deviceName = intent.getStringExtra(DEVICE_NAME);
            serviceThread.startWsClient(serverIp, deviceName);
        }

        return START_NOT_STICKY; //todo: or sticky?
    }

    @Override
    public void onDestroy() {
        serviceThread.quit();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    private class RebroServiceThread extends HandlerThread implements OnSocketClosedListener {

        private Handler handler;
        private RealmManager realmManager;
        private WsClient wsClient;

        RebroServiceThread() {
            super("RebroServiceThread", THREAD_PRIORITY_BACKGROUND);
        }

        void startWsClient(@NonNull final String serverIp, @NonNull final String deviceName) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    realmManager = new RealmManager();
                    realmManager.setOnDataChangeListener(new IRealmManager.DataChangedListener() {
                        @Override
                        public void onDataChanged() {
                            pushAll();
                        }
                    });
                    wsClient = new WsClient(serverIp, realmManager, deviceName);
                    wsClient.setOnSocketClosedListener(RebroServiceThread.this);
                    wsClient.connect();
                }
            });
        }

        private void pushAll() {
            wsClient.pushAll();
        }

        @Override
        public synchronized void start() {
            super.start();
            handler = new Handler(getLooper());
        }

        @Override
        public void onSocketClosed() {
            stopSelf();
        }
    }

}
