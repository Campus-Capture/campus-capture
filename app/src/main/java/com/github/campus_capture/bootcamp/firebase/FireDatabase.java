package com.github.campus_capture.bootcamp.firebase;

import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.authentication.Section;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class FireDatabase{

    private static final FirebaseDatabase db = FirebaseDatabase.getInstance();

    /**
     * Add the user to the database if absent.
     */
    public static void initUser(Section section) {
        DatabaseReference dbRef = db.getReference("Users/"+ User.getUid());

        Map<String, String> data = new HashMap<>();
        data.put("name", User.getName());
        data.put("section", section.name());

        dbRef.setValue(data);

    }

    /*
     * Return the name of the user.
     */
    /*public static void getUser() {
        DatabaseReference dbRef = db.getReference("Users/"+User.getUid());

        // Read from the database
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                HashMap<String, String> value = dataSnapshot.getValue(HashMap.class);
                if(value!=null) {
                    User.setName(value.get("name"));
                }
                Log.w(TAG, "Value: "+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }*/
}
