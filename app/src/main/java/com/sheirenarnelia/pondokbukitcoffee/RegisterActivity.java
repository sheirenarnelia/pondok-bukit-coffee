package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFullName, tilRegisterEmail, tilRegisterPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etRegisterEmail, etRegisterPassword, etConfirmPassword;
    private MaterialButton btnRegisterSubmit;
    private TextView tvGoToLogin;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userManager = new UserManager(this);

        // Initialize Views
        tilFullName = findViewById(R.id.tilFullName);
        tilRegisterEmail = findViewById(R.id.tilRegisterEmail);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etFullName = findViewById(R.id.etFullName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Auto-clear error when user types
        setupErrorClearing();

        // Submit Registration Action
        btnRegisterSubmit.setOnClickListener(v -> handleRegister());

        // Navigate back to Login Screen
        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupErrorClearing() {
        etFullName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilFullName.setError(null);
            }
        });

        etRegisterEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilRegisterEmail.setError(null);
            }
        });

        etRegisterPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilRegisterPassword.setError(null);
            }
        });

        etConfirmPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);
            }
        });
    }

    private void handleRegister() {
        tilFullName.setError(null);
        tilRegisterEmail.setError(null);
        tilRegisterPassword.setError(null);
        tilConfirmPassword.setError(null);

        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String email = etRegisterEmail.getText() != null ? etRegisterEmail.getText().toString().trim() : "";
        String password = etRegisterPassword.getText() != null ? etRegisterPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        boolean isValid = true;

        // Full Name Validation
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Full name is required");
            isValid = false;
        } else if (fullName.length() < 3) {
            tilFullName.setError("Full name must be at least 3 characters");
            isValid = false;
        }

        // Email Validation
        if (TextUtils.isEmpty(email)) {
            tilRegisterEmail.setError("Email address is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilRegisterEmail.setError("Please enter a valid email address");
            isValid = false;
        } else if (userManager.isEmailRegistered(email)) {
            tilRegisterEmail.setError("This email is already registered. Please sign in instead.");
            isValid = false;
        }

        // Password Validation
        if (TextUtils.isEmpty(password)) {
            tilRegisterPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilRegisterPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        // Confirm Password Validation
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Show loading state
        btnRegisterSubmit.setEnabled(false);
        btnRegisterSubmit.setText("Creating account...");

        final boolean[] isHandled = {false};
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            if (!isHandled[0] && !isFinishing()) {
                isHandled[0] = true;
                userManager.registerUser(email, password, fullName, "user");
                Toast.makeText(RegisterActivity.this, "Account created! Please sign in.", Toast.LENGTH_SHORT).show();
                navigateToLogin(email);
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 6000);

        // 1. Firebase Authentication Registration
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (isHandled[0] || isFinishing()) return;
                    isHandled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    String uid = authResult.getUser() != null ? authResult.getUser().getUid() : "";

                    // 2. Save user profile to Cloud Firestore in background (non-blocking)
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("uid", uid);
                    userData.put("fullName", fullName);
                    userData.put("email", email);
                    userData.put("role", "user");
                    userData.put("createdAt", FieldValue.serverTimestamp());

                    if (!uid.isEmpty()) {
                        FirebaseFirestore.getInstance().collection("users").document(uid).set(userData);
                    }

                    // 3. Cache locally & navigate immediately to Login
                    userManager.registerUser(email, password, fullName, "user");
                    Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    navigateToLogin(email);
                })
                .addOnFailureListener(e -> {
                    if (isHandled[0] || isFinishing()) return;
                    isHandled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText(R.string.btn_register);

                    if (e.getMessage() != null && e.getMessage().contains("already in use")) {
                        tilRegisterEmail.setError("This email is already registered. Please sign in instead.");
                        tilRegisterEmail.requestFocus();
                    } else {
                        // Offline / local backup fallback
                        userManager.registerUser(email, password, fullName, "user");
                        Toast.makeText(RegisterActivity.this, "Account created locally!", Toast.LENGTH_SHORT).show();
                        navigateToLogin(email);
                    }
                });
    }

    private void navigateToLogin(String email) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra("REGISTERED_EMAIL", email);
        startActivity(intent);
        finish();
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
