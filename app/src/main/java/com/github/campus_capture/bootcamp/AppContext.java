package com.github.campus_capture.bootcamp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class AppContext extends Application {

    private static AppContext appContext;
    private FirebaseDatabase db = null;

    private AuthUI authenticationUI = null;

    private FirebaseAuth mFirebaseAuth = null;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }

    public static AppContext getAppContext() {
        return appContext;
    }

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
