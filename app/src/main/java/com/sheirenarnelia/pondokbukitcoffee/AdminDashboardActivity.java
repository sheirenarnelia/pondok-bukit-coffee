
package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    private MaterialButton btnManageOrders;
    private MaterialButton btnManageMenu;
    private MaterialButton btnAdminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        btnManageOrders = findViewById(R.id.btnManageOrders);
        btnManageMenu = findViewById(R.id.btnManageMenu);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        // Open Admin Order List
        btnManageOrders.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this, AdminOrderListActivity.class);
            startActivity(intent);
        });

        btnManageMenu.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this, AdminMenuActivity.class);
            startActivity(intent);
        });

        // Logout
        btnAdminLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            CartManager.getInstance().clearCart();

            Intent intent = new Intent(
                    this, LoginActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}
