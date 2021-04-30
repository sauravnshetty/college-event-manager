package com.example.eventmanager;

public class EventRowItem {

    private String eventName;
    private String clubName;
    private String date;

    public EventRowItem(String eventName, String clubName, String date) {
        this.eventName = eventName;
        this.clubName = clubName;
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
