package com.rakpak.pak_parak.Model;

import android.app.Notification;

import com.google.firebase.messaging.RemoteMessage;

public class Sender {
    private String to;
    private  Notifaction notification;

    public Sender(String to, Notifaction notification) {
        this.to = to;
        this.notification = notification;
    }
}
