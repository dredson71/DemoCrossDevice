package com.example.democrossdevice.WebSocket;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    public  WebSocketServer(InetSocketAddress address){
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.i("SERVER OPEN","Arrive open");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conn.close();
        Log.i("SERVER CLOSE","Arrive close");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        broadcast(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.i("ERROR SERVER","Arrive Error");
    }

    @Override
    public void onStart() {

    }

}
