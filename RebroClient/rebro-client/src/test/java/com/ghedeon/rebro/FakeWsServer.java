package com.ghedeon.rebro;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.UUID;

class FakeWsServer extends WebSocketServer {

    private IWsServerLogger logger;

    FakeWsServer(IWsServerLogger logger) throws UnknownHostException {
        super(new InetSocketAddress(8887));
        this.logger = logger;
    }

    @Override
    public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
        logger.onOpen();
    }

    @Override
    public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
        logger.onClose();
    }

    @Override
    public void onMessage(final WebSocket conn, final String message) {
        System.out.printf("lorem mess " + message);
        logger.onMessage(message);
    }

    @Override
    public void onError(final WebSocket conn, final Exception ex) {
        logger.onError();
    }

    void send(final RpcMethod method) {
        final JSONRPC2Request request = new JSONRPC2Request(method.name(), UUID.randomUUID());
        for (final WebSocket webSocket : connections()) {
            webSocket.send(request.toString());
        }
    }
}
