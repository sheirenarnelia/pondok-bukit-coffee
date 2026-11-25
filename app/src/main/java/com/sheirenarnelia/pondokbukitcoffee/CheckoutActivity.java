package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextInputLayout tilTableNumber, tilPickupName;
    private TextInputEditText etTableNumber, etPickupName, etOrderNotes;
    private CheckBox cbStraw, cbSeparateIce;
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

        tilTableNumber = findViewById(R.id.tilTableNumber);
        tilPickupName = findViewById(R.id.tilPickupName);
        etTableNumber = findViewById(R.id.etTableNumber);
        etPickupName = findViewById(R.id.etPickupName);
        etOrderNotes = findViewById(R.id.etOrderNotes);

        cbStraw = findViewById(R.id.cbStraw);
        cbSeparateIce = findViewById(R.id.cbSeparateIce);
        spPaymentMethod = findViewById(R.id.spPaymentMethod);

        tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        tvCheckoutTax = findViewById(R.id.tvCheckoutTax);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);

        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnBackCheckout = findViewById(R.id.btnBackCheckout);

        // Setup Payment Spinner (Selection - Syarat Dosen #2c)
        setupPaymentSpinner();

        // Setup Dynamic Order Type toggle (RadioGroup - Syarat Dosen #2d)
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
                "QRIS / E-Wallet (GoPay, OVO, ShopeePay)",
                "Debit / Credit Card"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, paymentOptions);
        spPaymentMethod.setAdapter(adapter);
    }

    private void setupOrderTypeListener() {
        rgOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDineIn) {
                tilTableNumber.setVisibility(View.VISIBLE);
                tilPickupName.setVisibility(View.GONE);
                tilPickupName.setError(null);
            } else if (checkedId == R.id.rbPickup) {
                tilTableNumber.setVisibility(View.GONE);
                tilPickupName.setVisibility(View.VISIBLE);
                tilTableNumber.setError(null);
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
        tilTableNumber.setError(null);
        tilPickupName.setError(null);

        boolean isDineIn = rbDineIn.isChecked();
        String orderDestination;

        if (isDineIn) {
            String tableNo = etTableNumber.getText() != null ? etTableNumber.getText().toString().trim() : "";
            if (TextUtils.isEmpty(tableNo)) {
                tilTableNumber.setError("Please enter your table number");
                tilTableNumber.requestFocus();
                return;
            }
            orderDestination = "Dine-in (" + tableNo + ")";
        } else {
            String pickupName = etPickupName.getText() != null ? etPickupName.getText().toString().trim() : "";
            if (TextUtils.isEmpty(pickupName)) {
                tilPickupName.setError("Please enter your name or phone number for pickup");
                tilPickupName.requestFocus();
                return;
            }
            orderDestination = "Pickup for " + pickupName;
        }

        String selectedPayment = spPaymentMethod.getSelectedItem().toString();
        int totalAmount = CartManager.getInstance().getTotalPrice();

        // Show Success Confirmation Dialog
        new AlertDialog.Builder(this)
                .setTitle("Order Placed Successfully! ☕")
                .setMessage("Thank you for ordering at Pondok Bukit Coffee!\n\n" +
                        "• Order Type: " + orderDestination + "\n" +
                        "• Payment: " + selectedPayment + "\n" +
                        "• Total Amount: " + currencyFormatter.format(totalAmount) + "\n\n" +
                        "Your order has been sent to the barista and is being prepared.")
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
