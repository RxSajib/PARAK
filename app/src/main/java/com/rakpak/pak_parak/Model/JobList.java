package com.rakpak.pak_parak.Model;

public class JobList {

    private String Achievement, Date, Email, EmploymentStatus, File, Firstname, Goals, Lastname, Time, WhatsAppNumber;

    public JobList() {
    }

    public JobList(String achievement, String date, String email, String employmentStatus, String file, String firstname, String goals, String lastname, String time, String whatsAppNumber) {
        Achievement = achievement;
        Date = date;
        Email = email;
        EmploymentStatus = employmentStatus;
        File = file;
        Firstname = firstname;
        Goals = goals;
        Lastname = lastname;
        Time = time;
        WhatsAppNumber = whatsAppNumber;
    }

    public String getAchievement() {
        return Achievement;
    }

    public void setAchievement(String achievement) {
        Achievement = achievement;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getEmploymentStatus() {
        return EmploymentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        EmploymentStatus = employmentStatus;
    }

    public String getFile() {
        return File;
    }

    public void setFile(String file) {
        File = file;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getGoals() {
        return Goals;
    }

    public void setGoals(String goals) {
        Goals = goals;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getWhatsAppNumber() {
        return WhatsAppNumber;
    }

    public void setWhatsAppNumber(String whatsAppNumber) {
        WhatsAppNumber = whatsAppNumber;
    }
}
