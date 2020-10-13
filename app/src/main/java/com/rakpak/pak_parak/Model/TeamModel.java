package com.rakpak.pak_parak.Model;

public class TeamModel {
    private String date, designation, name, time, uri;

    public TeamModel() {
    }

    public TeamModel(String date, String designation, String name, String time, String uri) {
        this.date = date;
        this.designation = designation;
        this.name = name;
        this.time = time;
        this.uri = uri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
