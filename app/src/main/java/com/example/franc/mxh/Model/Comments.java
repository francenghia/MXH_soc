package com.example.franc.mxh.Model;

public class Comments {
    private String comment,date,time,name,image;

    public Comments() {
    }

    public Comments(String comment, String date, String time, String name, String image) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.name = name;
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
