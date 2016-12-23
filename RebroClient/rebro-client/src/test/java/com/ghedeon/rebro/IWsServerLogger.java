package com.ghedeon.rebro;


interface IWsServerLogger {
    void onOpen();

    void onClose();

    void onMessage(String message);

    void onError();
}
