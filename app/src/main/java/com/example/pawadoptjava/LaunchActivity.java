package com.example.pawadoptjava;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Check if there is a saved token
        String token = getTokenFromSharedPreferences();

        // If there is a token, launch the MainActivity
        if (token != null && !token.isEmpty()) {
            launchMainActivity();
        } else {
            // If there is no token, launch the LoginActivity
            launchLoginActivity();
        }
    }

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", null);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Finish the LaunchActivity so the user cannot navigate back to it
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the LaunchActivity so the user cannot navigate back to it
    }
}
