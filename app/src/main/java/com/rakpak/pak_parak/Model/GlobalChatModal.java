package com.rakpak.pak_parak.Model;

public class GlobalChatModal {

    private String message, name, time, date, type;

    public GlobalChatModal() {
    }

    public GlobalChatModal(String message, String name, String time, String date, String type) {
        this.message = message;
        this.name = name;
        this.time = time;
        this.date = date;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}