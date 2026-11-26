package com.sheirenarnelia.pondokbukitcoffee;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrderHistory;
    private TextView tvEmptyHistory;
    private ImageButton btnBackHistory;

    private final List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        btnBackHistory = findViewById(R.id.btnBackHistory);

        btnBackHistory.setOnClickListener(v -> finish());

        loadSampleOrders(); // TODO Phase 4: replace with Firestore query where userId == current user

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(this));
        rvOrderHistory.setAdapter(new OrderHistoryAdapter(orders));

        boolean empty = orders.isEmpty();
        tvEmptyHistory.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvOrderHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void loadSampleOrders() {
        orders.add(new Order("001", "Sheirena", "Dine-in", "05", "",
                "Less sweet please", Order.STATUS_DIPROSES, 88000, new Date(),
                Arrays.asList(Order.line("Signature Latte", 2, "Large"))));

        orders.add(new Order("002", "Sheirena", "Pickup", "", "Sheirena",
                "", Order.STATUS_SIAP, 36300, new Date(),
                Arrays.asList(Order.line("Caramel Macchiato", 1, "Regular"))));

        orders.add(new Order("003", "Sheirena", "Dine-in", "12", "",
                "", Order.STATUS_SELESAI, 27500, new Date(),
                Arrays.asList(Order.line("Signature Latte", 1, "Regular"))));
    }
}