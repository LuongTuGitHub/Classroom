package com.application.project.classroom.object;

import java.util.List;

public class Person {
    private String emailUser,passwordHashCode,nameUser,userUUID,work;
    private List<Ratting> timeRatting;

    public double getRatting() {
        if(timeRatting==null){
            return 5.0;
        }
        int sum = 0;
        for (int i = 0; i < timeRatting.size(); i++) {
            sum+= timeRatting.get(i).getStart();
        }
        return (double) sum/timeRatting.size();
    }

    public List<Ratting> getTimeRatting() {
        return timeRatting;
    }

    public void setTimeRatting(List<Ratting> timeRatting) {
        this.timeRatting = timeRatting;
    }


    private int assets = -1;


    public void setWork(String work) {
        this.work = work;
    }

    public String getWork() {
        return work;
    }

    private List<String> courses;

    public Person(){

    }

    public int getAssets() {
        if(assets==-1){
            return  0;
        }
        return assets;
    }

    public void setAssets(int assets) {
        this.assets = assets;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public Person(String emailUser, String passwordHashCode, String nameUser, String userUUID) {
        this.emailUser = emailUser;
        this.passwordHashCode = passwordHashCode;
        this.nameUser = nameUser;
        this.userUUID = userUUID;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public void setPasswordHashCode(String passwordHashCode) {
        this.passwordHashCode = passwordHashCode;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public String getPasswordHashCode() {
        return passwordHashCode;
    }
}
