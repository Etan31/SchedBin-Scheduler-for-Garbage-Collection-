package com.example.a1project;

public class Schedule {

    private String date;
    private String address;
    private String garbageType;
    private String repeatType;
    private String dayOfWeek;
    private String month;

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
    public Schedule(String date, String address, String garbageType, String repeatType, String dayOfWeek, String month) {
        this.date = date;
        this.address = address;
        this.garbageType = garbageType;
        this.repeatType = repeatType;
        this.dayOfWeek = dayOfWeek;
        this.month = month;
    }

    // Getter and setter methods for the new properties
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
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
