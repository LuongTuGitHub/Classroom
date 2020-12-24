package com.application.project.classroom.object;

public class Page {
    private String text,imageUUID,idYoutube;

    public Page(String text, String imageUUID, String idYoutube) {
        this.text = text;
        this.imageUUID = imageUUID;
        this.idYoutube = idYoutube;
    }

    public Page() {
    }

    public Page(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUUID() {
        return imageUUID;
    }

    public void setImageUUID(String imageUUID) {
        this.imageUUID = imageUUID;
    }

    public String getIdYoutube() {
        return idYoutube;
    }

    public void setIdYoutube(String idYoutube) {
        this.idYoutube = idYoutube;
    }
}
