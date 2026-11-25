package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private MaterialButton btnRegisterSubmit;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Views
        etFullName = findViewById(R.id.etFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Submit Registration Action
        btnRegisterSubmit.setOnClickListener(v -> handleRegister());

        // Navigate back to Login Screen
        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegister() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etRegisterEmail.getText() != null ? etRegisterEmail.getText().toString().trim() : "";
        String password = etRegisterPassword.getText() != null ? etRegisterPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        // Form Validation
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etRegisterEmail.setError("Email address is required");
            etRegisterEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etRegisterPassword.setError("Password is required");
            etRegisterPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etRegisterPassword.setError("Password must be at least 6 characters");
            etRegisterPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Temporary registration feedback before Firebase is integrated
        Toast.makeText(this, "Registration successful for: " + fullName, Toast.LENGTH_SHORT).show();

        // Note: In Day 2, FirebaseAuth createUserWithEmailAndPassword will be placed here.
    }
}
