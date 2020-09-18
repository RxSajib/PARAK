package com.rakpak.pak_parak.Model;

public class HistoryModel {

    private String ImageUri, Date, Time, Name, Text, Type, ID;

    public HistoryModel() {
    }

    public HistoryModel(String imageUri, String date, String time, String name, String text, String type, String ID) {
        ImageUri = imageUri;
        Date = date;
        Time = time;
        Name = name;
        Text = text;
        Type = type;
        this.ID = ID;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
