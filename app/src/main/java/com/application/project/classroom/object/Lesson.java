package com.application.project.classroom.object;

import java.util.List;

public class Lesson {
    private String title;
    private List<Comment> comments;
    private List<Page> pages;

    public Lesson(String title, List<Comment> comments, List<Page> pages) {
        this.title = title;
        this.comments = comments;
        this.pages = pages;
    }

    public Lesson() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}
