package com.github.Jenjamin3000.bootcamp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    /**
     * Create the options menu when initializing the toolbar
     * @param menu The options menu in which you place your items.
     *
     * @return success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the toolbar to add the items to the tool bar if they are present
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_navigate:
                // TODO: open the navigation screen
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}