package com.rakpak.pak_parak.Model;

public class HistoryModel {

    private String ImageUri, Date, Time, Name, Text;

    public HistoryModel() {
    }

    public HistoryModel(String imageUri, String date, String time, String name, String text) {
        ImageUri = imageUri;
        Date = date;
        Time = time;
        Name = name;
        Text = text;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
