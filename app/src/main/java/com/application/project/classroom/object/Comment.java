package com.application.project.classroom.object;

public class Comment {
    private String person;
    private String content;
    private String date;

    public Comment() {
    }

    public Comment(String person, String content, String date) {
        this.person = person;
        this.content = content;
        this.date = date;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return date;
    }

    public void setData(String date) {
        this.date = date;
    }
}
