package com.rhix.newsplashactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load shared preferences (dark mode option)
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean darkMode = sharedPreferences.getBoolean("dark_mode", false);
        // set default value to false to trigger login
        boolean loginUser = sharedPreferences.getBoolean("user_login", false);
        Toast.makeText(this, String.valueOf(loginUser), Toast.LENGTH_SHORT).show();
        // remove ! to disable login activity
        if (!loginUser) {
            Intent i = new Intent(MainActivity.this, LoginProcess.class);
            startActivity(i);
            finish();
        }

        if (darkMode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
        setContentView(R.layout.activity_main);
        // For search bar
        SearchView searchView = findViewById(R.id.search_view);
        // Use reflection to access the internal EditText of SearchView

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //getSupportActionBar().setTitle("Custom Title");

        // Set up NavigationView and its item selection listener
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load the default fragment (if needed)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FragmentHome())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
        }

        // Bottom Navigation Item Selection Listener
        // Updated method to set the item selected listener
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    selectedFragment = new FragmentHome();
                }else if(id == R.id.nav_products){
                    selectedFragment = new FragmentProducts();
                }else if(id == R.id.nav_food){
                    selectedFragment = new FragmentFood();
                }else if(id == R.id.nav_graphs){
                    selectedFragment = new FragmentGraphs();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });

        // Handle search query submission
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query
                Toast.makeText(MainActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                // Perform search or any related action here
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text changes in the search bar
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        // Determine which fragment to show based on the menu item selected
        if (id == R.id.nav_profile) {
            fragment = new FragmentProfile();
        } else if (id == R.id.nav_settings) {
            fragment = new FragmentSettings();  // Add more fragments as needed
        } else if (id == R.id.nav_logout) {
            logoutUser(); // logout
        }

        // Replace the fragment if it exists
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }

        // Close the navigation drawer after an item is selected
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logoutUser() {
        // Clear user login state from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("user_login", false);
        editor.apply();

        // Redirect to LoginProcess activity
        Intent intent = new Intent(MainActivity.this, LoginProcess.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
        startActivity(intent);
        finish(); // Close the current activity
    }


}