package com.sheirenarnelia.pondokbukitcoffee;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
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
    private LinearLayout llTemperature;
    private RadioGroup rgTemperature, rgSelection;
    private RadioButton rbIced, rbHot, rbOption1, rbOption2;
    private TextView tvSelectionTitle, tvAddonsTitle;
    private CheckBox cbAddon1, cbAddon2, cbAddon3;
    private EditText etNotes;
    private TextView btnMinus, tvQuantity, btnPlus;
    private MaterialButton btnAddToCart;

    private MenuItem menuItem;
    private boolean isCoffee = false;
    private boolean isNonCoffee = false;
    private boolean isFood = false;

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
        setupDynamicForm();
        setupListeners();
        updatePriceDisplay();
    }

    private void initViews() {
        ivDetailPhoto = findViewById(R.id.ivDetailPhoto);
        btnBack = findViewById(R.id.btnBack);
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        llTemperature = findViewById(R.id.llTemperature);
        rgTemperature = findViewById(R.id.rgTemperature);
        rbIced = findViewById(R.id.rbIced);
        rbHot = findViewById(R.id.rbHot);

        tvSelectionTitle = findViewById(R.id.tvSelectionTitle);
        tvAddonsTitle = findViewById(R.id.tvAddonsTitle);
        rgSelection = findViewById(R.id.rgSelection);
        rbOption1 = findViewById(R.id.rbOption1);
        rbOption2 = findViewById(R.id.rbOption2);

        cbAddon1 = findViewById(R.id.cbAddon1);
        cbAddon2 = findViewById(R.id.cbAddon2);
        cbAddon3 = findViewById(R.id.cbAddon3);
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

            String category = menuItem.getCategory() != null ? menuItem.getCategory() : "";
            isCoffee = category.equalsIgnoreCase("Coffee");
            isNonCoffee = category.equalsIgnoreCase("Non-Coffee");
            isFood = category.equalsIgnoreCase("Main Course") || category.equalsIgnoreCase("Snacks");

            if (menuItem.getImageUrl() != null && !menuItem.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(menuItem.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivDetailPhoto);
            }
        }
    }

    private void setupDynamicForm() {
        if (isCoffee) {
            // Minuman Kopi: Ada Hot/Ice
            llTemperature.setVisibility(View.VISIBLE);
            tvSelectionTitle.setText("Cup Size");
            rbOption1.setText("Regular (Standard 12 oz)");
            rbOption2.setText("Large (+Rp 4.000)");

            tvAddonsTitle.setText("Coffee Add-ons");
            cbAddon1.setText("Extra Espresso Shot (+Rp 5.000)");
            cbAddon2.setText("Oat Milk Substitute (+Rp 7.000)");
            cbAddon3.setVisibility(View.GONE);
            etNotes.setHint("e.g., Less sweet, extra ice, normal foam...");

        } else if (isNonCoffee) {
            // Minuman Non-Kopi: Ada Hot/Ice + Topping Boba/Cream Cheese
            llTemperature.setVisibility(View.VISIBLE);
            tvSelectionTitle.setText("Cup Size");
            rbOption1.setText("Regular (Standard 12 oz)");
            rbOption2.setText("Large (+Rp 4.000)");

            tvAddonsTitle.setText("Special Toppings");
            cbAddon1.setText("Cream Cheese (+Rp 5.000)");
            cbAddon2.setText("Chewy Boba (+Rp 4.000)");
            cbAddon3.setVisibility(View.VISIBLE);
            cbAddon3.setText("Sea Salt Cream (+Rp 5.000)");
            etNotes.setHint("e.g., Less ice, 50% sugar...");

        } else {
            // Makanan (Main Course & Snacks): Sembunyikan Hot/Ice!
            llTemperature.setVisibility(View.GONE);
            tvSelectionTitle.setText("Portion & Spiciness");
            rbOption1.setText("Regular (Mild / Normal)");
            rbOption2.setText("Jumbo Portion (+Rp 4.000)");

            tvAddonsTitle.setText("Side Dishes & Add-ons");
            cbAddon1.setText("Extra Steamed Rice / Nasi (+Rp 5.000)");
            cbAddon2.setText("Extra Fried Egg / Telur (+Rp 5.000)");
            cbAddon3.setVisibility(View.VISIBLE);
            cbAddon3.setText("Iced Sweet Tea / Es Teh Manis (+Rp 5.000)");
            etNotes.setHint("e.g., Extra spicy, sauce separated, no onions...");
        }
    }

    private void setupListeners() {
        rgSelection.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbOption2) {
                sizeExtra = 4000;
            } else {
                sizeExtra = 0;
            }
            updatePriceDisplay();
        });

        cbAddon1.setOnCheckedChangeListener((buttonView, isChecked) -> calculateAddons());
        cbAddon2.setOnCheckedChangeListener((buttonView, isChecked) -> calculateAddons());
        cbAddon3.setOnCheckedChangeListener((buttonView, isChecked) -> calculateAddons());

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

        btnAddToCart.setOnClickListener(v -> saveItemToCart());
    }

    private void calculateAddons() {
        addonsExtra = 0;

        if (isCoffee) {
            if (cbAddon1.isChecked()) addonsExtra += 5000; // Extra Shot
            if (cbAddon2.isChecked()) addonsExtra += 7000; // Oat Milk
        } else if (isNonCoffee) {
            if (cbAddon1.isChecked()) addonsExtra += 5000; // Cream Cheese
            if (cbAddon2.isChecked()) addonsExtra += 4000; // Boba
            if (cbAddon3.isChecked()) addonsExtra += 5000; // Sea Salt Cream
        } else {
            if (cbAddon1.isChecked()) addonsExtra += 5000; // Nasi
            if (cbAddon2.isChecked()) addonsExtra += 5000; // Telur
            if (cbAddon3.isChecked()) addonsExtra += 5000; // Es Teh Manis
        }

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
        String selectedOption = (rgSelection.getCheckedRadioButtonId() == R.id.rbOption2) ?
                (isFood ? "Jumbo" : "Large") : "Regular";

        String selectedTemperature = "N/A";
        if (isCoffee || isNonCoffee) {
            selectedTemperature = (rgTemperature.getCheckedRadioButtonId() == R.id.rbHot) ? "Hot" : "Iced";
        }

        StringBuilder addons = new StringBuilder();
        if (isCoffee) {
            if (cbAddon1.isChecked()) addons.append("Extra Shot, ");
            if (cbAddon2.isChecked()) addons.append("Oat Milk, ");
        } else if (isNonCoffee) {
            if (cbAddon1.isChecked()) addons.append("Cream Cheese, ");
            if (cbAddon2.isChecked()) addons.append("Boba, ");
            if (cbAddon3.isChecked()) addons.append("Sea Salt Cream, ");
        } else {
            if (cbAddon1.isChecked()) addons.append("Extra Nasi, ");
            if (cbAddon2.isChecked()) addons.append("Extra Telur, ");
            if (cbAddon3.isChecked()) addons.append("Es Teh Manis, ");
        }
        String addonString = addons.length() > 0 ? addons.substring(0, addons.length() - 2) : "None";

        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) notes = "-";

        String userId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "guest_user";

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("menuId", menuItem != null ? menuItem.getId() : "item");
        cartItem.put("name", menuItem != null ? menuItem.getName() : "Item");
        cartItem.put("unitPrice", basePrice + sizeExtra + addonsExtra);
        cartItem.put("quantity", quantity);
        cartItem.put("totalPrice", totalPrice);
        cartItem.put("temperature", selectedTemperature);
        cartItem.put("optionSize", selectedOption);
        cartItem.put("addons", addonString);
        cartItem.put("notes", notes);
        cartItem.put("timestamp", System.currentTimeMillis());

        // Add to local CartManager so CartActivity shows it immediately
        java.util.List<String> addonsList = new java.util.ArrayList<>();
        if (!"None".equals(addonString)) {
            for (String a : addonString.split(", ")) {
                addonsList.add(a);
            }
        }
        String sizeDisplay = !"N/A".equals(selectedTemperature)
                ? selectedTemperature + " • " + selectedOption
                : selectedOption;

        CartItem localCartItem = new CartItem(
                menuItem != null ? menuItem.getId() : "m1",
                menuItem != null ? menuItem.getName() : "Pondok Bukit Coffee",
                sizeDisplay,
                addonsList,
                notes,
                (int) (basePrice + sizeExtra + addonsExtra),
                quantity
        );
        CartManager.getInstance().addItem(localCartItem);

        // Save to Firestore under 'cart' collection in background (non-blocking)
        db.collection("users").document(userId)
                .collection("cart")
                .add(cartItem);

        Toast.makeText(this, "Added " + quantity + "x " + (menuItem != null ? menuItem.getName() : "Item") + " to cart!", Toast.LENGTH_SHORT).show();
        finish();
    }
}