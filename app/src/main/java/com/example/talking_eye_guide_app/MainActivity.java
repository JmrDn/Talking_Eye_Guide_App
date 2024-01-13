package com.example.talking_eye_guide_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.talking_eye_guide_app.BottomNav.HistoryFragment;
import com.example.talking_eye_guide_app.BottomNav.HomeFragment;
import com.example.talking_eye_guide_app.BottomNav.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private final String defaultNav = "Home";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();

        Fragment selectedFragment = null;

        if (defaultNav.equals("Home")){
            selectedFragment = new HomeFragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if(itemId == R.id.Home){
                    selectedFragment = new HomeFragment();
                }
                else if (itemId == R.id.History){
                    selectedFragment = new HistoryFragment();
                }
                else if (itemId == R.id.Profile){
                    selectedFragment = new ProfileFragment();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container, selectedFragment).commit();
                return true;
            }
        });

        
       
    }

    private void initWidgets() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}