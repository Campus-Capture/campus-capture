package com.github.campus_capture.bootcamp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class AppContext extends Application {
    private FirebaseDatabase db = null;

    public FirebaseDatabase getFirebaseDB(){
        if(db == null) {
            db = FirebaseDatabase.getInstance();

            // enable caching
            db.setPersistenceEnabled(true);
        }
        return db;
    }
}
