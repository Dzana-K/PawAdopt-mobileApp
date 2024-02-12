package com.example.pawadoptjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class AdoptionForm extends AppCompatActivity {
    private ApiService apiService;
    private EditText emailEditText;
    public int userId;
    public String animalName;
    private EditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adoption_form);
        ImageView backArrowImageView = findViewById(R.id.backArrowImageView);

        // Initialize ApiService
        apiService = new ApiService(this);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        // Retrieve user ID from Intent extras
        userId = getIntent().getIntExtra("userId", -1);
        animalName = getIntent().getStringExtra("animalName");
        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });
        // Fetch user details and send email

    }

    public void fetchUserAndSendEmail(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = apiService.getUser(userId);
                    String email = emailEditText.getText().toString();
                    String phone = phoneEditText.getText().toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (user != null) {
                                String recipientEmail = user.getEmail();

                                String body = "Hello, You have received an adoption inquiry for your pet. Details: Pet Name:  " + animalName + " Interested Person's Email: " + email + ", Interested Person's Phone Number: " + phone + ". Plase reach out to them at your earliest convenience. Thank you,PawAdopt";
                                Log.d("EmailDebug", "Recipient Email: " + body);
                                sendEmail(recipientEmail, "PawAdopt", body);
                            } else {
                                // Handle case where user is null
                                Toast.makeText(AdoptionForm.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendEmail(String recipientEmail, String subject, String body) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiService.sendEmail(recipientEmail, subject, body);
                    runOnUiThread(new Runnable() {


                        @Override
                        public void run() {

                            Toast.makeText(AdoptionForm.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AdoptionForm.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}