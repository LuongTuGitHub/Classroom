package com.application.project.classroom.object;

public class Ratting {
    private int start;
    private String person;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
    Ratting(){

    }

    public Ratting(int start, String person) {
        this.start = start;
        this.person = person;
    }

    public Ratting(int start) {
        this.start = start;
    }
}
