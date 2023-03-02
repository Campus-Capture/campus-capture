package com.github.Jenjamin3000.bootcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity_main); // Open the navigation

        // Create the toolbar and add the navigation icon
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24);
        setSupportActionBar(toolbar);

        // Set the behavior of the navigation icon
        drawer = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(v -> {
            if(!drawer.isOpen())
            {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        openFragment(true);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * Fragment manager
     */
    private void openFragment(boolean isMain)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isMain)
        {
            fragmentTransaction.replace(R.id.fragmentContainerViewMain, new MainFragment());
        }
        else
        {
            fragmentTransaction.replace(R.id.fragmentContainerViewMain, new GreetingFragment());
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

    /**
     * Method to close the drawer
     */
    private void closeDrawer()
    {
        if(drawer.isOpen())
        {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_main:
                openFragment(true);
                break;
            case R.id.nav_greeting:
                openFragment(false);
                break;
            default:
                break;
        }
        closeDrawer();
        return true;
    }
}