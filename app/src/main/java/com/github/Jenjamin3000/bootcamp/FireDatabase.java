package com.github.Jenjamin3000.bootcamp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FireDatabase implements SDPDatabase{


    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private final TextView email;
    private final TextView phone;
    private final TextView answer;

    public FireDatabase(TextView email, TextView phone, TextView answer){
        this.email = email;
        this.phone = phone;
        this.answer = answer;
    }


    public void get(View view){

        DatabaseReference myRef = database.getReference(phone.getText().toString());


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
                Toast.makeText(view.getContext(), "Value is: " +value, Toast.LENGTH_SHORT).show();
                answer.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void set(View view){
        String emailText = email.getText().toString();
        String phoneText = phone.getText().toString();

        DatabaseReference myRef = database.getReference(phoneText);

        myRef.setValue(emailText);

    }

}
