package com.example.a1project;

public class Schedule {

    private String date;
    private String address;
    private String garbageType;
    private String repeatType;
    private String startTime;
    private String endTime;

    public Schedule() {
        // Default constructor required for Firebase
    }

    public Schedule(String date, String address, String garbageType, String repeatType) {
        this.date = date;
        this.address = address;
        this.garbageType = garbageType;
        this.repeatType = repeatType;
    }

    // Constructor with additional properties
    public Schedule(String date, String address, String garbageType, String repeatType, String startTime, String endTime) {
        this.date = date;
        this.address = address;
        this.garbageType = garbageType;
        this.repeatType = repeatType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter and setter methods for the new properties
    public String getDayOfWeek() {
        return startTime;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.startTime = startTime;
    }

    public String getMonth() {
        return endTime;
    }

    public void setMonth(String month) {
        this.endTime = endTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGarbageType() {
        return garbageType;
    }

    public void setGarbageType(String garbageType) {
        this.garbageType = garbageType;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}
