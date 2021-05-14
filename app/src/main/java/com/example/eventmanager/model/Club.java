package com.example.eventmanager.model;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Club implements Serializable {

    private String name;
    private String branch;
    private String introText;

    private String clubId;

    public Club() {

    }

    public Club(String name, String branch, String introText) {
        this.name = name;
        this.branch = branch;
        this.introText = introText;
    }

    @Exclude
    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }
}
