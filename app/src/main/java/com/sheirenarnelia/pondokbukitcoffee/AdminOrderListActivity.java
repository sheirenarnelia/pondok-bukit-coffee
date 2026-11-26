package com.sheirenarnelia.pondokbukitcoffee;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdminOrderListActivity extends AppCompatActivity {

    private RecyclerView rvAdminOrders;
    private TextView tvEmptyAdmin;
    private ImageButton btnBackAdmin;
    private AdminOrderAdapter adapter;

    private final List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        rvAdminOrders = findViewById(R.id.rvAdminOrders);
        tvEmptyAdmin = findViewById(R.id.tvEmptyAdmin);
        btnBackAdmin = findViewById(R.id.btnBackAdmin);

        btnBackAdmin.setOnClickListener(v -> finish());

        loadSampleOrders(); // TODO Phase 4: replace with Firestore real-time listener

        rvAdminOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(orders, this::confirmStatusUpdate);
        rvAdminOrders.setAdapter(adapter);

        updateEmptyState();
    }

    private void loadSampleOrders() {
        orders.add(new Order("001", "Sheirena", "Dine-in", "05", "",
                "Less sweet please", Order.STATUS_DIPROSES, 88000, new Date(),
                Arrays.asList(Order.line("Signature Latte", 2, "Large"))));

        orders.add(new Order("002", "Budi", "Pickup", "", "Budi",
                "", Order.STATUS_SIAP, 36300, new Date(),
                Arrays.asList(Order.line("Caramel Macchiato", 1, "Regular"))));

        orders.add(new Order("003", "Dewi", "Dine-in", "12", "",
                "", Order.STATUS_SELESAI, 27500, new Date(),
                Arrays.asList(Order.line("Signature Latte", 1, "Regular"))));
    }

    private void confirmStatusUpdate(Order order) {
        String next = order.getNextStatus();
        if (next == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Update order status")
                .setMessage("Order #" + order.getShortId() + ": "
                        + order.getStatusLabel() + " → " + labelOf(next) + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", (dialog, which) -> {
                    // TODO Phase 4: update Firestore document "orders/<id>" field "status"
                    order.setStatus(next);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Status updated to " + labelOf(next), Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private String labelOf(String status) {
        if (Order.STATUS_SIAP.equals(status)) return "Ready";
        if (Order.STATUS_SELESAI.equals(status)) return "Completed";
        return "Processing";
    }

    private void updateEmptyState() {
        boolean empty = orders.isEmpty();
        tvEmptyAdmin.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvAdminOrders.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}