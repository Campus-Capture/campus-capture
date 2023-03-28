package com.github.campus_capture.bootcamp.activities;

import static com.github.campus_capture.bootcamp.fragments.Fragments.GREETING_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.MAIN_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.MAPS_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.PROFILE_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.RULES_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.SCOREBOARD_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.TEST_FRAGMENT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.firebase.FirebaseInterface;
import com.github.campus_capture.bootcamp.firebase.PlaceholderFirebaseInterface;
import com.github.campus_capture.bootcamp.fragments.Fragments;
import com.github.campus_capture.bootcamp.fragments.GreetingFragment;
import com.github.campus_capture.bootcamp.fragments.MainFragment;
import com.github.campus_capture.bootcamp.fragments.MapsFragment;
import com.github.campus_capture.bootcamp.fragments.ProfileFragment;
import com.github.campus_capture.bootcamp.fragments.RulesFragment;
import com.github.campus_capture.bootcamp.fragments.ScoreboardFragment;
import com.github.campus_capture.bootcamp.fragments.TestFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static FirebaseInterface firebaseInterface;

    /**
     * Required empty constructor, which will set the placeholder as the back-end
     */
    public MainActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_activity_main); // Open the navigation

        // Create the toolbar and add the navigation icon
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.menu_icon);
        setSupportActionBar(toolbar);

        // TODO replace once the firebase access has actually been implemented
        if(firebaseInterface == null)
        {
            firebaseInterface = new PlaceholderFirebaseInterface();
        }

        // Set the behavior of the navigation icon
        drawer = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(v -> {
            if(!drawer.isOpen())
            {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Open the main fragment inside the fragment container
        openFragment(MAIN_FRAGMENT);

        // Add a listener for the navigation drawer
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);
    }

    /**
     * Method which opens a given fragment
     * @param fragment The fragment to be switched
     */
    private void openFragment(Fragments fragment)
    {
        // Fragments are managed by transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(fragment)
        {
            case MAIN_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new MainFragment());
                break;

            case GREETING_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new GreetingFragment());
                break;

            case MAPS_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new MapsFragment(firebaseInterface));
                break;

            case TEST_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new TestFragment());
                break;

            case PROFILE_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new ProfileFragment());
                break;

            case RULES_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new RulesFragment());
                break;

            case SCOREBOARD_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new ScoreboardFragment(firebaseInterface));
                break;

            default:
                return;
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // Commit the transaction
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
     * Method to close the navigation drawer if it is open
     */
    private void closeDrawer()
    {
        if(drawer.isOpen())
        {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * When an item is selected in the navigation drawer, switch to the corresponding fragment
     * and close the drawer
     * @param item The selected item
     * @return success
     */

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.nav_main:
                openFragment(MAIN_FRAGMENT);
                break;
            case R.id.nav_greeting:
                openFragment(GREETING_FRAGMENT);
                break;
            case R.id.nav_maps:
                openFragment(MAPS_FRAGMENT);
                break;
            case R.id.nav_test:
                openFragment(TEST_FRAGMENT);
                break;
            case R.id.nav_profile:
                openFragment(PROFILE_FRAGMENT);
                break;
            case R.id.nav_rules:
                openFragment(RULES_FRAGMENT);
                break;
            case R.id.nav_scoreboard:
                openFragment(SCOREBOARD_FRAGMENT);
                break;
            default:
                break;
        }
        closeDrawer();
        return true;
    }

    /**
     * Method to catch when an item in the options is selected
     * @param item The menu item that was selected.
     *
     * @return success
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_logout:

                Intent log_in_intent = new Intent(this, AuthenticationActivity.class);

                // Use this to pass the name of the origin activity
                //log_in_intent.putExtra("message", "From: " + FirstActivity.class.getSimpleName());

                startActivity(log_in_intent);

            default:
                break;
        }
        return true;
    }
}