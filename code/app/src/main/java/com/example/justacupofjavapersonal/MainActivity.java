package com.example.justacupofjavapersonal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.justacupofjavapersonal.databinding.ActivityMainBinding;

/**
 * Entry point of the application.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    
    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int) to programmatically interact
     * with widgets in the UI, and initializing other components.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup top toolbar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        // Enable back navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize navigation controller
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Setup navigation bar configuration
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_feed, R.id.navigation_notifications)
                .build();
        NavigationUI.setupWithNavController(binding.topAppBar, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

//            if (itemId == R.id.navigation_home) {
//                // If already on home, clear the back stack and go to home
//                navController.popBackStack(R.id.navigation_home, false);
//                navController.navigate(R.id.navigation_home);
//                return true;
//            }

            return NavigationUI.onNavDestinationSelected(item, navController);
        });


        // Ensure the app starts on the login page
        navController.navigate(R.id.navigation_login);

        // Hide bottom navigation on the login screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_login) {
                binding.navView.setVisibility(View.GONE);  // Hide bottom navigation
            } else {
                binding.navView.setVisibility(View.VISIBLE);  // Show bottom navigation elsewhere
            }
        });

        // Handle toolbar back navigation
        topAppBar.setNavigationOnClickListener(v -> navController.navigateUp());
    }

    /**
     * Initializes the contents of the Activity's options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return true for the menu to be displayed; false if it should not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    /**
     * Handles item selections from the options menu.
     *
     * @param item The selected menu item.
     * @return true if the item selection was handled, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navController.navigateUp();
            return true;
        } else if (item.getItemId() == R.id.action_user_info) {
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() != R.id.navigation_user_info) {
                navController.navigate(R.id.navigation_user_info);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles the navigation when the user chooses to navigate up within the app's activity hierarchy from the action bar.
     *
     * @return true if navigation was successful, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
