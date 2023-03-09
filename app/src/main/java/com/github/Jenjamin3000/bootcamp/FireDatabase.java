package com.github.Jenjamin3000.bootcamp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FireDatabase{

    private static final FirebaseDatabase db = FirebaseDatabase.getInstance();

    /**
     * Add the user to the database if absent.
     */
    public static void addUserIfAbsent() {
        DatabaseReference dbRef = db.getReference(User.getUid());

        Map<String, String> datas = new HashMap<>();
        datas.put("name", User.getName());

        dbRef.setValue(datas);

    }

    /**
     * Return the username of the user.
     */
    public static DatabaseReference getUserName() {
        DatabaseReference dbRef = db.getReference(User.getUid());

        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.w(TAG, "Value: "+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return dbRef;
    }
}
