package com.application.project.classroom.object;

import java.util.List;

public class Course {
    private String courseUUID,courseID,nameCourse,introductionCourse;
    private String teacher;
    private int price = -1;
    private List<String> persons;
    private List<Ratting> timeRatting;
    private List<Week> weeks;
    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Week> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Week> weeks) {
        this.weeks = weeks;
    }

    public void setTimeRatting(List<Ratting> timeRatting) {
        this.timeRatting = timeRatting;
    }


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

    public List<String> getPersons() {
        return persons;
    }

    public void setPersons(List<String> persons) {
        this.persons = persons;
    }

    public Course() {
    }

    public int getPrice() {
        if(price==-1){
            return 0;
        }
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Course(String teacher, String courseUUID, String courseID, String nameCourse, String introductionCourse) {
        this.teacher = teacher;
        this.courseUUID = courseUUID;
        this.courseID = courseID;
        this.nameCourse = nameCourse;
        this.introductionCourse = introductionCourse;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getCourseUUID() {
        return courseUUID;
    }

    public void setCourseUUID(String courseUUID) {
        this.courseUUID = courseUUID;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getNameCourse() {
        return nameCourse;
    }

    public void setNameCourse(String nameCourse) {
        this.nameCourse = nameCourse;
    }

    public String getIntroductionCourse() {
        return introductionCourse;
    }

    public void setIntroductionCourse(String introductionCourse) {
        this.introductionCourse = introductionCourse;
    }
}
