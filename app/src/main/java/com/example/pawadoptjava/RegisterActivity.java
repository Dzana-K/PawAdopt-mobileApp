package com.example.pawadoptjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, firstNameEditText, lastNameEditText, addressEditText, cityEditText, countryEditText;
    private Button registerButton;
    private TextView register_link;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = new AuthService(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        addressEditText = findViewById(R.id.address);
        cityEditText = findViewById(R.id.city);
        countryEditText = findViewById(R.id.country);
        registerButton = findViewById(R.id.registerButton);
        register_link= findViewById(R.id.register_link);
        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void register(View view) {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String country = countryEditText.getText().toString().trim();
        authService.register(email, password, firstName, lastName, address, city, country, new AuthService.LoginCallback() {
            @Override
            public void onSuccess() {

                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Refister successful", Toast.LENGTH_SHORT).show());
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();



            }

            @Override
            public void onFailure(String errorMessage) {
                // Registration failed, display error message
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
