package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private RadioGroup rgOrderType;
    private RadioButton rbDineIn, rbPickup;
    private TextInputLayout tilCustomerName;
    private TextInputEditText etCustomerName, etOrderNotes;
    private LinearLayout llTakeawayPreferences, llCashView, llQrisView;
    private CheckBox cbCutlery;
    private Spinner spPaymentMethod;
    private TextView tvCheckoutSubtotal, tvCheckoutTax, tvCheckoutTotal;
    private MaterialButton btnConfirmOrder;
    private ImageButton btnBackCheckout;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize Views
        rgOrderType = findViewById(R.id.rgOrderType);
        rbDineIn = findViewById(R.id.rbDineIn);
        rbPickup = findViewById(R.id.rbPickup);

        tilCustomerName = findViewById(R.id.tilCustomerName);
        etCustomerName = findViewById(R.id.etCustomerName);
        etOrderNotes = findViewById(R.id.etOrderNotes);

        llTakeawayPreferences = findViewById(R.id.llTakeawayPreferences);
        llCashView = findViewById(R.id.llCashView);
        llQrisView = findViewById(R.id.llQrisView);
        cbCutlery = findViewById(R.id.cbCutlery);
        spPaymentMethod = findViewById(R.id.spPaymentMethod);

        tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        tvCheckoutTax = findViewById(R.id.tvCheckoutTax);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);

        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnBackCheckout = findViewById(R.id.btnBackCheckout);

        // Setup 2 Payment Options (Cash vs QRIS)
        setupPaymentSpinner();

        // Setup Dynamic Order Type toggle (Dine-in vs Takeaway)
        setupOrderTypeListener();

        // Populate Price Summary
        populateSummary();

        // Back Button
        btnBackCheckout.setOnClickListener(v -> finish());

        // Confirm Order Action
        btnConfirmOrder.setOnClickListener(v -> handlePlaceOrder());
    }

    private void setupPaymentSpinner() {
        String[] paymentOptions = {
                "Pay at Cashier (Cash)",
                "QRIS / Instant E-Wallet"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paymentOptions);
        spPaymentMethod.setAdapter(adapter);

        // Toggle between Cash message and QRIS view
        spPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Cash selected
                    llCashView.setVisibility(View.VISIBLE);
                    llQrisView.setVisibility(View.GONE);
                } else {
                    // QRIS selected
                    llCashView.setVisibility(View.GONE);
                    llQrisView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupOrderTypeListener() {
        rgOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDineIn) {
                // Dine-in: hide cutlery preferences
                llTakeawayPreferences.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbPickup) {
                // Takeaway: show cutlery preferences
                llTakeawayPreferences.setVisibility(View.VISIBLE);
            }
        });
    }

    private void populateSummary() {
        CartManager cm = CartManager.getInstance();
        tvCheckoutSubtotal.setText(currencyFormatter.format(cm.getSubtotal()));
        tvCheckoutTax.setText(currencyFormatter.format(cm.getTax()));
        tvCheckoutTotal.setText(currencyFormatter.format(cm.getTotalPrice()));
    }

    private void handlePlaceOrder() {
        tilCustomerName.setError(null);

        String customerName = etCustomerName.getText() != null ? etCustomerName.getText().toString().trim() : "";
        if (TextUtils.isEmpty(customerName)) {
            tilCustomerName.setError("Order name is required");
            tilCustomerName.requestFocus();
            return;
        }

        boolean isDineIn = rbDineIn.isChecked();
        String orderType = isDineIn ? "Dine-in" : "Takeaway / Pickup";
        String selectedPayment = spPaymentMethod.getSelectedItem().toString();
        int totalAmount = CartManager.getInstance().getTotalPrice();

        StringBuilder details = new StringBuilder();
        details.append("Thank you for your order!\n\n");
        details.append("• Name: ").append(customerName).append("\n");
        details.append("• Order Type: ").append(orderType).append("\n");
        if (!isDineIn) {
            details.append("• Cutlery & Bag: ").append(cbCutlery.isChecked() ? "Yes" : "No").append("\n");
        }
        String notes = etOrderNotes.getText() != null ? etOrderNotes.getText().toString().trim() : "";
        if (!TextUtils.isEmpty(notes)) {
            details.append("• Notes: \"").append(notes).append("\"\n");
        }
        details.append("• Payment: ").append(selectedPayment).append("\n");
        details.append("• Total Amount: ").append(currencyFormatter.format(totalAmount)).append("\n\n");
        details.append("Your order has been sent to the barista and will be ready shortly.");

        // Show Success Confirmation Dialog
        new AlertDialog.Builder(this)
                .setTitle("Order Placed Successfully! ☕")
                .setMessage(details.toString())
                .setPositiveButton("Back to Home", (dialog, which) -> {
                    // Clear the cart
                    CartManager.getInstance().clearCart();
                    // Go back to MainActivity
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
}
