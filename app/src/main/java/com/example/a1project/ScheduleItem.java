package com.example.a1project;

public class ScheduleItem {
    private String address;
    private String date;
    private String garbageType;
    private String repeatType;
    private String time;

    public ScheduleItem(String address, String date, String garbageType, String repeatType, String time) {
        this.address = address;
        this.date = date;
        this.garbageType = garbageType;
        this.repeatType = repeatType;
        this.time = time;
    }
}
