package com.ghedeon.rebro;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Message;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class WsClient extends WebSocketClient {

    private static final String TAG = WsClient.class.getSimpleName();

    private static final int SERVER_PORT = 8887; //todo: send it from server via intent?

    private static final String CONNECTION_ID = "CONNECTION_ID";
    private static final String DEVICE_NAME_PARAM = "DEVICE_NAME";

    @NonNull
    private final IRealmManager realmManager;
    @Nullable
    private OnSocketClosedListener listener;
    @NonNull
    private final String deviceName;
    private String connectionId;

    interface OnSocketClosedListener {

        void onSocketClosed();
    }

    WsClient(@NonNull final String serverIP, @NonNull final IRealmManager realmManager, @NonNull final String deviceName) {
        super(URI.create("ws://" + serverIP + ":" + SERVER_PORT));
        if (BuildConfig.DEBUG) {
            WebSocketImpl.DEBUG = true;
        }
        this.realmManager = realmManager;
        this.deviceName = deviceName;
    }

    @Override
    public void onOpen(@NonNull final ServerHandshake handshakedata) {
        Thread.currentThread().setName("WsClientThread");
//        Log.d(TAG, "connection opened: " + handshakedata.getHttpStatusMessage());
        connectionId = UUID.randomUUID().toString();
        sendConnectRequest();
    }

    private void sendConnectRequest() {
        final UUID uuid = UUID.randomUUID();
        final Map<String, Object> params = new HashMap<>();
        params.put(CONNECTION_ID, connectionId);
        params.put(DEVICE_NAME_PARAM, deviceName);
        final JSONRPC2Request request = new JSONRPC2Request(RpcMethod.CONNECT.name(), params, uuid);
        send(request.toString());
    }

    @Override
    public void onMessage(@NonNull final String message) {
//        Log.d(TAG, "received: " + message);
        System.out.println(message);
        try {
            final JSONRPC2Message jsonMessage = JSONRPC2Message.parse(message);
            if (jsonMessage instanceof JSONRPC2Request) {
                onHandleRequest(((JSONRPC2Request) jsonMessage));
            } else if (jsonMessage instanceof JSONRPC2Response) {
                onHandleResponse(((JSONRPC2Response) jsonMessage));
            } else if (jsonMessage instanceof JSONRPC2Notification) {
                onHandleNotification(((JSONRPC2Notification) jsonMessage));
            }
        } catch (@NonNull final JSONRPC2ParseException e) {
            //TODO error handling
            e.printStackTrace();
        }
    }

    private void onHandleRequest(@NonNull final JSONRPC2Request request) {
        final RpcMethod rpcMethod = RpcMethod.valueOf(request.getMethod());
        switch (rpcMethod) {
            case LIST:
                final List<RTable> RTables = realmManager.list();
                final ListResponse listResponse = new ListResponse();
                listResponse.setConnectionId(connectionId);
                listResponse.setDbName(realmManager.getRealmFileName());
                listResponse.setTables(RTables);
                final JSONRPC2Response response = new JSONRPC2Response(listResponse, request.getID());
                send(response.toString());
//                Log.d(TAG, "send: " + response.toString());
                break;
            default:
        }
    }

    private void onHandleResponse(@NonNull final JSONRPC2Response response) {

    }

    private void onHandleNotification(@NonNull final JSONRPC2Notification notification) {
        final RpcMethod rpcMethod = RpcMethod.valueOf(notification.getMethod());
        switch (rpcMethod) {
            case DISCONNECT:
                close();
        }
    }

    void pushAll() {
        final List<RTable> RTables = realmManager.list();
        final ListResponse listResponse = new ListResponse();
        listResponse.setConnectionId(connectionId);
        listResponse.setTables(RTables);

        final Map<String, Object> params = new HashMap<>();
        params.put("data", listResponse);

        final JSONRPC2Notification pushNotification = new JSONRPC2Notification(RpcMethod.PUSH.name(), params);
        send(pushNotification.toString());
//        Log.d(TAG, "pushAll: " + pushNotification.toString());
    }

    @Override
    public void onClose(final int code, @NonNull final String reason, final boolean remote) {
//        Log.d(TAG, "connection closed by " + (remote ? "remote peer" : "us"));
        realmManager.close();
        if (listener != null) {
            listener.onSocketClosed();
        }
    }

    @Override
    public void onError(@NonNull final Exception ex) {
        //TODO: handle
        ex.printStackTrace();
        // if the error is fatal then close will be called additionally
    }

    void setOnSocketClosedListener(@Nullable final OnSocketClosedListener listener) {
        this.listener = listener;
    }
}