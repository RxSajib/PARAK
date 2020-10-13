package com.rakpak.pak_parak.Model;

import android.app.Notification;

public class Notifaction {

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Notifaction(String body, String title) {

        this.body = body;
        this.title = title;
    }

    private String body;
    private String title;
}
