package com.example.pawadoptjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private AuthService authService;
    private TextView register_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        register_link= findViewById(R.id.register_link);
        authService = new AuthService(this);

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        authService.login(email, password, new AuthService.LoginCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                // Proceed to next screen
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
