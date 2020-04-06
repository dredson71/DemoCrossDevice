package com.example.democrossdevice.Entities;


import java.util.Date;
import java.util.List;

public class SocketMessage {
    private String sender;
    private String receiver;
    private String message;
    private boolean pairing;
    private Date time;

    public SocketMessage()
    {
        pairing = false;
        time = new Date();
        receiver = "";
    }
    public SocketMessage(String sender, String receiver, String message, boolean pairing, Date time) {
        this.sender = sender;
        this.receiver = receiver;
        this.pairing = pairing;
        this.time = time;
    }
    public String getSender() {
        return sender;
    }

    public SocketMessage setSender(String sender) {
        this.sender = sender;
        return this;
    }
    public String getReceiver() {
        return receiver;
    }
    public SocketMessage setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }
    public String getMessage() {
        return message;
    }

    public SocketMessage setMessage(String message) {
        this.message = message;
        return this;
    }
    public boolean isPairing() {
        return pairing;
    }

    public SocketMessage setPairing(boolean pairing) {
        this.pairing = pairing;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }



}
