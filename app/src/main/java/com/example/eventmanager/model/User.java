package com.example.eventmanager.model;

public class User {

    private String email;
    private String name;
    private String usn;
    private int sem;
    private boolean isAdmin;

    public User() {

    }

    public User(String email, String name, String usn, int sem, boolean isAdmin) {
        this.email = email;
        this.name = name;
        this.usn = usn;
        this.sem = sem;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public int getSem() {
        return sem;
    }

    public void setSem(int sem) {
        this.sem = sem;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
