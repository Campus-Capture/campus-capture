package com.github.campus_capture.bootcamp.activities;

import static com.github.campus_capture.bootcamp.fragments.Fragments.ABOUT_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.MAPS_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.POWER_UP_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.RULES_FRAGMENT;
import static com.github.campus_capture.bootcamp.fragments.Fragments.SCOREBOARD_FRAGMENT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.campus_capture.bootcamp.AppContext;
import com.github.campus_capture.bootcamp.R;
import com.github.campus_capture.bootcamp.authentication.User;
import com.github.campus_capture.bootcamp.firebase.BackendInterface;
import com.github.campus_capture.bootcamp.firebase.FirebaseBackend;
import com.github.campus_capture.bootcamp.fragments.AboutFragment;
import com.github.campus_capture.bootcamp.fragments.Fragments;
import com.github.campus_capture.bootcamp.fragments.MapsFragment;
import com.github.campus_capture.bootcamp.fragments.RulesFragment;
import com.github.campus_capture.bootcamp.fragments.ScoreboardFragment;
import com.github.campus_capture.bootcamp.fragments.VotePowerUpFragment;
import com.github.campus_capture.bootcamp.map.SectionColors;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static BackendInterface backendInterface = new FirebaseBackend();

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

        // Set the behavior of the navigation icon
        drawer = findViewById(R.id.drawer_layout);
        toolbar.setNavigationOnClickListener(v -> {
            if(!drawer.isOpen())
            {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        // Open the main fragment inside the fragment container
        openFragment(MAPS_FRAGMENT);

        // Add a listener for the navigation drawer
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        //If the user is not registered, the shop button will be made invisible
        if(User.getUid() == null){
            navView.getMenu().findItem(R.id.nav_power_up).setVisible(false);
        }
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
            case MAPS_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new MapsFragment(backendInterface));
                break;

            case RULES_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new RulesFragment());
                break;

            case SCOREBOARD_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new ScoreboardFragment(backendInterface));
                break;

            case POWER_UP_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new VotePowerUpFragment(backendInterface));
                break;

            case ABOUT_FRAGMENT:
                fragmentTransaction.replace(R.id.fragmentContainerViewMain, new AboutFragment());
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
            case R.id.nav_maps:
                openFragment(MAPS_FRAGMENT);
                break;
            case R.id.nav_rules:
                openFragment(RULES_FRAGMENT);
                break;
            case R.id.nav_scoreboard:
                openFragment(SCOREBOARD_FRAGMENT);
                break;
            case R.id.nav_power_up:
                openFragment(POWER_UP_FRAGMENT);
                break;
            case R.id.nav_about:
                openFragment(ABOUT_FRAGMENT);
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
                logOut();
                break;
            case R.id.action_invite:
                sendInvite();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * Set up and launch the activity to send an invitation
     */
    private void sendInvite() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String message = getString(R.string.invitation_text) + getString(R.string.invitation_link);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        // Use a chooser for better visuals (the default send intent is ugly)
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    /**
     * Performs all the necessary operation needed on the logOut of the user
     */
    private void logOut() {
        Intent log_in_intent = new Intent(this, AuthenticationActivity.class);
        User.setName(null);
        User.setUid(null);
        User.setSection(null);

        AppContext context = (AppContext) getApplicationContext();
        context.getFirebaseAuth().signOut();

        // Use this to pass the name of the origin activity
        //log_in_intent.putExtra("message", "From: " + FirstActivity.class.getSimpleName());

        startActivity(log_in_intent);
    }
}