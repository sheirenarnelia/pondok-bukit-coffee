package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLoginSubmit;
    private TextView tvGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Submit Login Action
        btnLoginSubmit.setOnClickListener(v -> handleLogin());

        // Navigate to Register Screen
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Form Validation
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email address is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        // Temporary auth feedback before Firebase is integrated
        Toast.makeText(this, "Signing in as: " + email, Toast.LENGTH_SHORT).show();
        
        // Note: In Day 2, FirebaseAuth signInWithEmailAndPassword & Firestore role checking will be placed here.
    }
}
