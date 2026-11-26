
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderListActivity extends AppCompatActivity {

    private RecyclerView rvAdminOrders;
    private TextView tvEmptyAdmin;
    private ImageButton btnBackAdmin;
    private AdminOrderAdapter adapter;

    private FirebaseFirestore db;
    private ListenerRegistration orderListener;
    private boolean adminVerified = false;

    private final List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_list);

        db = FirebaseFirestore.getInstance();

        rvAdminOrders = findViewById(R.id.rvAdminOrders);
        tvEmptyAdmin = findViewById(R.id.tvEmptyAdmin);
        btnBackAdmin = findViewById(R.id.btnBackAdmin);

        btnBackAdmin.setOnClickListener(v -> finish());

        rvAdminOrders.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new AdminOrderAdapter(
                orders, this::confirmStatusUpdate);

        rvAdminOrders.setAdapter(adapter);

        updateEmptyState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyAdmin();
    }

    private void verifyAdmin() {
        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            tvEmptyAdmin.setText("Please sign in with a Firebase admin account.");
            return;
        }

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (isFinishing() ||
                            !getLifecycle().getCurrentState().isAtLeast(
                                    androidx.lifecycle.Lifecycle.State.STARTED)) {
                        return;
                    }

                    if ("admin".equals(document.getString("role"))) {
                        adminVerified = true;
                        loadOrders();
                    } else {
                        tvEmptyAdmin.setText("Admin access required.");
                    }
                })
                .addOnFailureListener(e ->
                        tvEmptyAdmin.setText("Unable to verify admin access."));
    }

    private void loadOrders() {
        if (orderListener != null) {
            orderListener.remove();
        }

        orderListener = db.collection("orders")
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        orders.clear();
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                        tvEmptyAdmin.setText("Failed to load orders.");
                        return;
                    }

                    if (snapshot == null) return;

                    orders.clear();

                    for (DocumentSnapshot document : snapshot) {
                        Order order = document.toObject(Order.class);

                        if (order != null) {
                            order.setId(document.getId());
                            orders.add(order);
                        }
                    }

                    orders.sort((a, b) -> {
                        if (a.getCreatedAt() == null)
                            return b.getCreatedAt() == null ? 0 : 1;
                        if (b.getCreatedAt() == null) return -1;
                        return b.getCreatedAt()
                                .compareTo(a.getCreatedAt());
                    });

                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
    }

    private void confirmStatusUpdate(Order order) {

        if (!adminVerified) return;

        String next = order.getNextStatus();

        if (next == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Update order status")
                .setMessage("Order #" + order.getShortId()
                        + ": " + order.getStatusLabel()
                        + " → " + labelOf(next) + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Yes", (dialog, which) -> {

                    db.collection("orders")
                            .document(order.getId())
                            .update("status", next)
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this,
                                            "Status updated to "
                                                    + labelOf(next),
                                            Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Update failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show());
                })
                .show();
    }

    private String labelOf(String status) {
        if (Order.STATUS_SIAP.equals(status))
            return "Ready";

        if (Order.STATUS_SELESAI.equals(status))
            return "Completed";

        return "Processing";
    }

    private void updateEmptyState() {
        boolean empty = orders.isEmpty();

        tvEmptyAdmin.setText("No orders yet");
        tvEmptyAdmin.setVisibility(
                empty ? View.VISIBLE : View.GONE);

        rvAdminOrders.setVisibility(
                empty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        adminVerified = false;

        if (orderListener != null) {
            orderListener.remove();
            orderListener = null;
        }

        orders.clear();
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }
}
