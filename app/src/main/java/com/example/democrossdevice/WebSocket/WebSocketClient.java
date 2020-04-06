package com.example.democrossdevice.WebSocket;

import android.util.Log;


import org.java_websocket.handshake.ServerHandshake;
import com.example.democrossdevice.InterfaceSocket.WebSocketReceiver;

import java.net.URI;


public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private WebSocketReceiver receiverMethod;

    public WebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public WebSocketClient(URI serverUri, WebSocketReceiver receiver) {
        super(serverUri);
        this.receiverMethod = receiver;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i("CLIENT OPEN","Arrive Open");
    }

    @Override
    public void onMessage(String message) {
        receiverMethod.onWebSocketMessage(message);
        Log.i("CLIENT MESSAGE:","Arrive Message");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        receiverMethod.onWebSocketClose(code,reason,remote);
        Log.i("CLIENT CLOSE:","Arrive Close");
    }

    @Override
    public void onError(Exception ex) {
        Log.i("CLIENT ERROR","Arrive error: "+ex.getMessage());
    }

}
