package com.example.eventmanager.model;

public class ClubRowItem {
    private String clubName;
    private String clubDept;

    public ClubRowItem() {
    }

    public ClubRowItem(String clubName, String clubDept) {
        this.clubName = clubName;
        this.clubDept = clubDept;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubDept() {
        return clubDept;
    }

    public void setClubDept(String clubDept) {
        this.clubDept = clubDept;
    }
}
