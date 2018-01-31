package com.example.kevin.infrastructureapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kevin on 12-1-2018.
 */

public class TheDatabase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // See https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase.html#public-methods
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Log.d("TheApp", "application created");
    }
}
