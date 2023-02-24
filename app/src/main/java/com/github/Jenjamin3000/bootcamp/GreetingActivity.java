package com.github.Jenjamin3000.bootcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GreetingActivity extends AppCompatActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        TextView greetingText = findViewById(R.id.greetingText);

        Intent intent = getIntent();
        String userNameText = intent.getStringExtra("userNameText"); //if it's a string you stored.

        greetingText.setText("Bonjour " + userNameText + "!");
    }
}
