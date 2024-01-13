package com.example.streamtv.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.streamtv.Fragments.Home;
import com.example.streamtv.Fragments.Profile;
import com.example.streamtv.Fragments.Search;
import com.example.streamtv.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        startActivity(loginIntent);
        finish(); // The user can't come back to this page
    }

    final FragmentManager fragmentManager = getSupportFragmentManager();
    final Fragment fragment1 = new Home();
    final Fragment fragment2 = new Search();
    final Fragment fragment3 = new Profile();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.home);


    }

    private BottomNavigationView.OnItemSelectedListener navListener =
            new NavigationBarView.OnItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.home:
                            fragment = fragment1;
                            break;
                        case R.id.search:
                            fragment = fragment2;
                            break;
                        case R.id.profile:
                        default:
                            fragment = fragment3;
                            break;
                    }
                    fragmentManager.beginTransaction().replace(R.id.rlContainer, fragment).commit();
                    return true;
                }
            };
    // Set default selection
//        BottomNavigationView.setSelectedItemId(R.id.home);
}