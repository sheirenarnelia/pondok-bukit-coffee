package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {
    private static final String PREF_NAME = "PondokBukitUsers";
    private static final String KEY_PASS_PREFIX = "pass_";
    private static final String KEY_NAME_PREFIX = "name_";
    private static final String KEY_ROLE_PREFIX = "role_";

    private final SharedPreferences prefs;

    public UserManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        seedDefaultAccounts();
    }

    private void seedDefaultAccounts() {
        // Seed default Admin account if not already present
        if (!isEmailRegistered("admin@pondokbukit.com")) {
            registerUser("admin@pondokbukit.com", "123456", "Store Manager", "admin");
        }
        // Seed default Customer account
        if (!isEmailRegistered("customer@pondokbukit.com")) {
            registerUser("customer@pondokbukit.com", "123456", "Loyal Customer", "user");
        }
    }

    public boolean isEmailRegistered(String email) {
        String key = KEY_PASS_PREFIX + email.toLowerCase().trim();
        return prefs.contains(key);
    }

    public boolean registerUser(String email, String password, String fullName, String role) {
        String normalizedEmail = email.toLowerCase().trim();
        if (isEmailRegistered(normalizedEmail) && !"admin".equalsIgnoreCase(role)) {
            return false; // Email already registered
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PASS_PREFIX + normalizedEmail, password);
        editor.putString(KEY_NAME_PREFIX + normalizedEmail, fullName);
        editor.putString(KEY_ROLE_PREFIX + normalizedEmail, role);
        editor.apply();
        return true;
    }

    public boolean checkPassword(String email, String password) {
        String normalizedEmail = email.toLowerCase().trim();
        String storedPassword = prefs.getString(KEY_PASS_PREFIX + normalizedEmail, null);
        return storedPassword != null && storedPassword.equals(password);
    }

    public String getUserRole(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return prefs.getString(KEY_ROLE_PREFIX + normalizedEmail, "user");
    }

    public String getUserFullName(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return prefs.getString(KEY_NAME_PREFIX + normalizedEmail, "Customer");
    }
}
