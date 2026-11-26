
package com.sheirenarnelia.pondokbukitcoffee;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private TextView tvEmptyHistory;
    private ImageButton btnBackHistory;

    private OrderHistoryAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration orderListener;

    private final List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        db = FirebaseFirestore.getInstance();

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        btnBackHistory = findViewById(R.id.btnBackHistory);

        btnBackHistory.setOnClickListener(v -> finish());

        rvOrderHistory.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new OrderHistoryAdapter(orders);
        rvOrderHistory.setAdapter(adapter);

        updateEmptyState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserOrders();
    }

    private void loadUserOrders() {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            tvEmptyHistory.setText("Please sign in to view your orders.");
            return;
        }

        if (orderListener != null) {
            orderListener.remove();
        }

        orderListener = db.collection("orders")
                .whereEqualTo("userId", user.getUid())
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        orders.clear();
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                        tvEmptyHistory.setText("Failed to load order history.");
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

    private void updateEmptyState() {

        boolean empty = orders.isEmpty();

        tvEmptyHistory.setText("No orders yet");

        tvEmptyHistory.setVisibility(
                empty ? View.VISIBLE : View.GONE);

        rvOrderHistory.setVisibility(
                empty ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (orderListener != null) {
            orderListener.remove();
            orderListener = null;
        }

        orders.clear();
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }
}
