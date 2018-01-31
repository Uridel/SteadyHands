package com.example.kevin.infrastructureapp;

/**
 * Created by Kevin on 24-1-2018.
 */

public class Time {
    public String username;


    public Time() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Time(String username) {
        this.username = username;
    }
}
