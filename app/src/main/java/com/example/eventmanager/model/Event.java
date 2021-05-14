package com.example.eventmanager.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

public class Event {

    private String eventId;
    private String eventName;
    private String eventClubName;
    private String eventDate;
    private String eventTime;
    private String eventVenue;
    private String eventDescription;
    private String eventOrganizers;
    private String eventClubId;

    public Event() {
    }

    public Event(String eventName, String eventClubName, String eventDate, String eventTime, String eventVenue, String eventDescription, String eventOrganizers,String eventClubId) {
        this.eventName = eventName;
        this.eventClubName = eventClubName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventVenue = eventVenue;
        this.eventDescription = eventDescription;
        this.eventOrganizers = eventOrganizers;
        this.eventClubId = eventClubId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventClubName() {
        return eventClubName;
    }

    public void setEventClubName(String eventClubName) {
        this.eventClubName = eventClubName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventOrganizers() {
        return eventOrganizers;
    }

    public void setEventOrganizers(String eventOrganizers) {
        this.eventOrganizers = eventOrganizers;
    }

    public String getEventClubId() {
        return eventClubId;
    }

    public void setEventClubId(String eventClubId) {
        this.eventClubId = eventClubId;
    }

}
