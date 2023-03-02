package com.github.Jenjamin3000.bootcamp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView emailText;
    TextView phoneText;
    TextView answerText;

    SDPDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getButton = findViewById(R.id.GetButton);
        Button setButton = findViewById(R.id.SetButton);
        emailText = findViewById(R.id.GetSetEmail);
        phoneText = findViewById(R.id.GetSetPhone);
        answerText = findViewById(R.id.GetSetAnswer);

        database = new MockDatabase(emailText, phoneText, answerText);

        getButton.setOnClickListener(database::get);

        setButton.setOnClickListener(database::set);
    }


}