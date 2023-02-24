package com.github.Jenjamin3000.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button mainGoButton = findViewById(R.id.mainGoButton);
        TextView userNameText = findViewById(R.id.mainUserNameText);

        mainGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, GreetingActivity.class);

                //Pass arguments
                myIntent.putExtra("userNameText", userNameText.getText().toString());

                MainActivity.this.startActivity(myIntent);
            }
        });
    }
}