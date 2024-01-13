package com.example.talking_eye_guide_app.Model;

public class HistoryModel {
    String location;
    String time;
    String date;

    public HistoryModel(String location, String time, String date) {
        this.location = location;
        this.time = time;
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
