package com.rakpak.pak_parak.Model;

public class TypeModel {

    private String Username;
    private String status;

    public TypeModel() {
    }

    public TypeModel(String username, String status) {
        Username = username;
        this.status = status;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
