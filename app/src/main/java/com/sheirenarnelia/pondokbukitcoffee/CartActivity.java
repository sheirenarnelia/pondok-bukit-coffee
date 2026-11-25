package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {

    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private LinearLayout llEmptyCart;
    private TextView tvCartSubtotal, tvCartTax, tvCartTotal;
    private MaterialButton btnProceedCheckout;
    private ImageButton btnBackCart;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Views
        rvCartItems = findViewById(R.id.rvCartItems);
        llEmptyCart = findViewById(R.id.llEmptyCart);
        tvCartSubtotal = findViewById(R.id.tvCartSubtotal);
        tvCartTax = findViewById(R.id.tvCartTax);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnProceedCheckout = findViewById(R.id.btnProceedCheckout);
        btnBackCart = findViewById(R.id.btnBackCart);

        // Setup RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(CartManager.getInstance().getCartItems(), this);
        rvCartItems.setAdapter(cartAdapter);

        // Update Summary
        updateCartSummary();

        // Back Button
        btnBackCart.setOnClickListener(v -> finish());

        // Proceed to Checkout Button
        btnProceedCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartItems().isEmpty()) {
                Toast.makeText(CartActivity.this, "Your cart is empty! Please select a coffee first.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void updateCartSummary() {
        CartManager cm = CartManager.getInstance();
        if (cm.getCartItems().isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            llEmptyCart.setVisibility(View.VISIBLE);
            btnProceedCheckout.setEnabled(false);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            llEmptyCart.setVisibility(View.GONE);
            btnProceedCheckout.setEnabled(true);
        }

        tvCartSubtotal.setText(currencyFormatter.format(cm.getSubtotal()));
        tvCartTax.setText(currencyFormatter.format(cm.getTax()));
        tvCartTotal.setText(currencyFormatter.format(cm.getTotalPrice()));
    }

    @Override
    public void onCartChanged() {
        updateCartSummary();
    }
}
