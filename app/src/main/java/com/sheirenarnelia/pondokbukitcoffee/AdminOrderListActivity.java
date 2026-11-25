package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AdminOrderListActivity extends AppCompatActivity {

    private MaterialButton btnAdminLogout, btnMarkReady1, btnMarkComplete2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnMarkReady1 = findViewById(R.id.btnMarkReady1);
        btnMarkComplete2 = findViewById(R.id.btnMarkComplete2);

        // Logout Action
        btnAdminLogout.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out from Admin Dashboard", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminOrderListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Sample Status Update Actions (Tugas Orang 3 nanti menghubungkan ke Firestore)
        btnMarkReady1.setOnClickListener(v -> {
            btnMarkReady1.setText("Order is Ready ✓");
            btnMarkReady1.setEnabled(false);
            Toast.makeText(this, "Order #ORD-101 marked as READY", Toast.LENGTH_SHORT).show();
        });

        btnMarkComplete2.setOnClickListener(v -> {
            btnMarkComplete2.setText("Order Completed ✓");
            btnMarkComplete2.setEnabled(false);
            Toast.makeText(this, "Order #ORD-100 marked as COMPLETED", Toast.LENGTH_SHORT).show();
        });
    }
}
