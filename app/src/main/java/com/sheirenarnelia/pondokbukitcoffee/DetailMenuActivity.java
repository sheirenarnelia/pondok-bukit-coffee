package com.sheirenarnelia.pondokbukitcoffee;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailMenuActivity extends AppCompatActivity {

    private ImageView ivDetailPhoto;
    private ImageButton btnBack;
    private TextView tvDetailName, tvDetailPrice, tvDetailDescription;
    private RadioGroup rgCupSize;
    private CheckBox cbExtraShot, cbOatMilk;
    private EditText etNotes;
    private TextView btnMinus, tvQuantity, btnPlus;
    private MaterialButton btnAddToCart;

    private MenuItem menuItem;
    private int quantity = 1;
    private long basePrice = 25000;
    private long sizeExtra = 0;
    private long addonsExtra = 0;
    private long totalPrice = 25000;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_menu);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initViews();
        extractIntentData();
        setupListeners();
        updatePriceDisplay();
    }

    private void initViews() {
        ivDetailPhoto = findViewById(R.id.ivDetailPhoto);
        btnBack = findViewById(R.id.btnBack);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        rgCupSize = findViewById(R.id.rgCupSize);
        cbExtraShot = findViewById(R.id.cbExtraShot);
        cbOatMilk = findViewById(R.id.cbOatMilk);
        etNotes = findViewById(R.id.etNotes);

        btnMinus = findViewById(R.id.btnMinus);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        btnBack.setOnClickListener(v -> finish());
    }

    private void extractIntentData() {
        if (getIntent() != null && getIntent().hasExtra("menu_item")) {
            menuItem = (MenuItem) getIntent().getSerializableExtra("menu_item");
        }

        if (menuItem != null) {
            tvDetailName.setText(menuItem.getName());
            tvDetailDescription.setText(menuItem.getDescription());
            basePrice = menuItem.getPrice();

            if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(menuItem.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivDetailPhoto);
            }
        }
    }

    private void setupListeners() {
        // Cup Size Selection Listener
        rgCupSize.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbLarge) {
                sizeExtra = 4000;
            } else {
                sizeExtra = 0;
            }
            updatePriceDisplay();
        });

        // Add-ons Checkbox Listeners
        cbExtraShot.setOnCheckedChangeListener((buttonView, isChecked) -> calculateAddons());
        cbOatMilk.setOnCheckedChangeListener((buttonView, isChecked) -> calculateAddons());

        // Quantity Stepper Listeners
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updatePriceDisplay();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (quantity < 50) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updatePriceDisplay();
            }
        });

        // Add to Cart Action
        btnAddToCart.setOnClickListener(v -> saveItemToCart());
    }

    private void calculateAddons() {
        addonsExtra = 0;
        if (cbExtraShot.isChecked()) addonsExtra += 5000;
        if (cbOatMilk.isChecked()) addonsExtra += 7000;
        updatePriceDisplay();
    }

    private void updatePriceDisplay() {
        long unitPrice = basePrice + sizeExtra + addonsExtra;
        totalPrice = unitPrice * quantity;

        String formattedBasePrice = "Rp " + String.format(Locale.GERMAN, "%,d", basePrice);
        tvDetailPrice.setText(formattedBasePrice);

        String formattedTotal = "Rp " + String.format(Locale.GERMAN, "%,d", totalPrice);
        btnAddToCart.setText("Add to Cart • " + formattedTotal);
    }

    private void saveItemToCart() {
        String selectedSize = (rgCupSize.getCheckedRadioButtonId() == R.id.rbLarge) ? "Large" : "Regular";
        StringBuilder addons = new StringBuilder();
        if (cbExtraShot.isChecked()) addons.append("Extra Shot, ");
        if (cbOatMilk.isChecked()) addons.append("Oat Milk, ");
        String addonString = addons.length() > 0 ? addons.substring(0, addons.length() - 2) : "None";

        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) notes = "-";

        String userId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "guest_user";

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("menuId", menuItem != null ? menuItem.getId() : "item");
        cartItem.put("name", menuItem != null ? menuItem.getName() : "Coffee");
        cartItem.put("unitPrice", basePrice + sizeExtra + addonsExtra);
        cartItem.put("quantity", quantity);
        cartItem.put("totalPrice", totalPrice);
        cartItem.put("cupSize", selectedSize);
        cartItem.put("addons", addonString);
        cartItem.put("notes", notes);
        cartItem.put("timestamp", System.currentTimeMillis());

        // Add to local CartManager so CartActivity shows it immediately
        java.util.List<String> addonsList = new java.util.ArrayList<>();
        if (cbExtraShot.isChecked()) addonsList.add("Extra Shot");
        if (cbOatMilk.isChecked()) addonsList.add("Oat Milk");

        CartItem localCartItem = new CartItem(
                menuItem != null ? menuItem.getId() : "m1",
                menuItem != null ? menuItem.getName() : "Pondok Bukit Coffee",
                selectedSize,
                addonsList,
                notes,
                (int) (basePrice + sizeExtra + addonsExtra),
                quantity
        );
        CartManager.getInstance().addItem(localCartItem);

        // Save to Firestore under 'cart' collection for user
        db.collection("users").document(userId)
                .collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Added " + quantity + "x " + (menuItem != null ? menuItem.getName() : "Item") + " to cart!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Added to cart locally!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
