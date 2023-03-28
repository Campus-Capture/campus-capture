package com.github.campus_capture.bootcamp;

import android.app.Application;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AppContext extends Application {
    private FirebaseDatabase db = null;

    private AuthUI authenticationUI = null;

    private FirebaseAuth mFirebaseAuth = null;

    public FirebaseDatabase getFirebaseDB(){
        if(db == null) {
            db = FirebaseDatabase.getInstance();

            // enable caching
            db.setPersistenceEnabled(true);
        }
        return db;
    }

    public FirebaseAuth getFirebaseAuth(){
        if(mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }
}
