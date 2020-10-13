package com.rakpak.pak_parak.Model;

public class JobsIteams {

    private  String company_name, contact_email, contact_number, job_descptrion, jobtitle, max_salary, min_salary;
    private String work_time, Current_Time, Current_Date;
    private JobsIteams(){

    }

    public JobsIteams(String company_name, String contact_email, String contact_number, String job_descptrion, String jobtitle, String max_salary, String min_salary, String work_time, String current_Time, String current_Date) {
        this.company_name = company_name;
        this.contact_email = contact_email;
        this.contact_number = contact_number;
        this.job_descptrion = job_descptrion;
        this.jobtitle = jobtitle;
        this.max_salary = max_salary;
        this.min_salary = min_salary;
        this.work_time = work_time;
        Current_Time = current_Time;
        Current_Date = current_Date;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getJob_descptrion() {
        return job_descptrion;
    }

    public void setJob_descptrion(String job_descptrion) {
        this.job_descptrion = job_descptrion;
    }

    public String getJobtitle() {
        return jobtitle;
    }

    public void setJobtitle(String jobtitle) {
        this.jobtitle = jobtitle;
    }

    public String getMax_salary() {
        return max_salary;
    }

    public void setMax_salary(String max_salary) {
        this.max_salary = max_salary;
    }

    public String getMin_salary() {
        return min_salary;
    }

    public void setMin_salary(String min_salary) {
        this.min_salary = min_salary;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    public String getCurrent_Time() {
        return Current_Time;
    }

    public void setCurrent_Time(String current_Time) {
        Current_Time = current_Time;
    }

    public String getCurrent_Date() {
        return Current_Date;
    }

    public void setCurrent_Date(String current_Date) {
        Current_Date = current_Date;
    }
}
