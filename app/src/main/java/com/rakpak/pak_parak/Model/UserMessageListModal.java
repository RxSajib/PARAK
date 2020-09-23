package com.rakpak.pak_parak.Model;

public class UserMessageListModal {

    private String Date, From, Message, Time, Type, To, Message_Id;

    public UserMessageListModal(){

    }


    public UserMessageListModal(String date, String from, String message, String time, String type, String to, String message_Id) {
        Date = date;
        From = from;
        Message = message;
        Time = time;
        Type = type;
        To = to;
        Message_Id = message_Id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getMessage_Id() {
        return Message_Id;
    }

    public void setMessage_Id(String message_Id) {
        Message_Id = message_Id;
    }
}
