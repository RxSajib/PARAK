package com.rakpak.pak_parak.Model;

public class UserIteamsList {

    private String Username;
    private String profileimage;
    private String last_message_reciver;

    public UserIteamsList(){

    }

    public UserIteamsList(String username, String profileimage, String last_message_reciver) {
        Username = username;
        this.profileimage = profileimage;
        this.last_message_reciver = last_message_reciver;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getLast_message_reciver() {
        return last_message_reciver;
    }

    public void setLast_message_reciver(String last_message_reciver) {
        this.last_message_reciver = last_message_reciver;
    }
}
