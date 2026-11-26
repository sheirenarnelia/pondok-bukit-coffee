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
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLoginSubmit;
    private TextView tvGoToRegister;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userManager = new UserManager(this);

        // Initialize Views
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // Check if redirected from RegisterActivity with prefilled email
        String registeredEmail = getIntent().getStringExtra("REGISTERED_EMAIL");
        if (registeredEmail != null && !registeredEmail.isEmpty()) {
            etEmail.setText(registeredEmail);
            etPassword.requestFocus();
        }

        // Auto-clear error when user types
        setupErrorClearing();

        // Submit Login Action
        btnLoginSubmit.setOnClickListener(v -> handleLogin());

        // Navigate to Register Screen
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupErrorClearing() {
        etEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
        });

        etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }
        });
    }

    private void handleLogin() {
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        boolean isValid = true;

        // Email Validation
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email address is required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address (e.g. name@domain.com)");
            isValid = false;
        }

        // Password Validation
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Show loading state
        btnLoginSubmit.setEnabled(false);
        btnLoginSubmit.setText("Signing in...");

        final boolean[] isHandled = {false};
        Handler timeoutHandler = new Handler(Looper.getMainLooper());
        Runnable timeoutRunnable = () -> {
            if (!isHandled[0] && !isFinishing()) {
                isHandled[0] = true;
                if (userManager.isEmailRegistered(email) && userManager.checkPassword(email, password)) {
                    String role = userManager.getUserRole(email);
                    String fullName = userManager.getUserFullName(email);
                    routeUserByRole(role, fullName);
                } else {
                    btnLoginSubmit.setEnabled(true);
                    btnLoginSubmit.setText(R.string.btn_login);
                    tilEmail.setError("Connection timeout. Please check your network and try again.");
                    tilEmail.requestFocus();
                }
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, 6000);

        // 1. Try Firebase Authentication first
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser() != null ? authResult.getUser().getUid() : "";

                    // Query role from Firestore
                    FirebaseFirestore.getInstance().collection("users").document(uid).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (isHandled[0] || isFinishing()) return;
                                isHandled[0] = true;
                                timeoutHandler.removeCallbacks(timeoutRunnable);

                                String role = documentSnapshot.exists() && documentSnapshot.getString("role") != null
                                        ? documentSnapshot.getString("role")
                                        : "user";
                                String fullName = documentSnapshot.exists() && documentSnapshot.getString("fullName") != null
                                        ? documentSnapshot.getString("fullName")
                                        : userManager.getUserFullName(email);

                                routeUserByRole(role, fullName);
                            })
                            .addOnFailureListener(e -> {
                                if (isHandled[0] || isFinishing()) return;
                                isHandled[0] = true;
                                timeoutHandler.removeCallbacks(timeoutRunnable);

                                // Firestore read failed, fallback to local role mapping
                                String role = userManager.getUserRole(email);
                                String fullName = userManager.getUserFullName(email);
                                routeUserByRole(role, fullName);
                            });
                })
                .addOnFailureListener(e -> {
                    if (isHandled[0] || isFinishing()) return;
                    isHandled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    // 2. Fallback to Local Auth (for pre-seeded demo accounts or offline test)
                    if (userManager.isEmailRegistered(email)) {
                        if (userManager.checkPassword(email, password)) {
                            String role = userManager.getUserRole(email);
                            String fullName = userManager.getUserFullName(email);
                            routeUserByRole(role, fullName);
                            return;
                        } else {
                            btnLoginSubmit.setEnabled(true);
                            btnLoginSubmit.setText(R.string.btn_login);
                            tilPassword.setError("Incorrect password. Please try again.");
                            tilPassword.requestFocus();
                            return;
                        }
                    }

                    btnLoginSubmit.setEnabled(true);
                    btnLoginSubmit.setText(R.string.btn_login);

                    String msg = e.getMessage() != null ? e.getMessage() : "Authentication failed";
                    if (msg.contains("no user record") || msg.contains("user-not-found") || msg.contains("INVALID_LOGIN_CREDENTIALS")) {
                        tilEmail.setError("Account not found or invalid credentials. Please register first.");
                        tilEmail.requestFocus();
                    } else if (msg.contains("password")) {
                        tilPassword.setError("Incorrect password. Please try again.");
                        tilPassword.requestFocus();
                    } else {
                        tilEmail.setError("Sign in error: " + msg);
                        tilEmail.requestFocus();
                    }
                });
    }

    private void routeUserByRole(String role, String fullName) {
        if ("admin".equalsIgnoreCase(role)) {
            Toast.makeText(this, "Welcome back, " + fullName + " (Admin)", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Signed in successfully as " + fullName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MenuListActivity.class);
            startActivity(intent);
        }
        finish();
    }


    // Helper text watcher
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
