
package com.sheirenarnelia.pondokbukitcoffee;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMenuActivity extends AppCompatActivity {

    private RecyclerView rvAdminMenu;
    private TextView tvEmptyMenu;
    private MaterialButton btnAddMenu;

    private FirebaseFirestore db;
    private ListenerRegistration menuListener;
    private AdminMenuAdapter adapter;

    private boolean adminVerified = false;

    private final List<MenuItem> menuList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        db = FirebaseFirestore.getInstance();

        rvAdminMenu = findViewById(R.id.rvAdminMenu);
        tvEmptyMenu = findViewById(R.id.tvEmptyMenu);
        btnAddMenu = findViewById(R.id.btnAddMenu);

        MaterialButton btnBack =
                findViewById(R.id.btnBackMenu);

        btnBack.setOnClickListener(v -> finish());

        rvAdminMenu.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new AdminMenuAdapter(menuList,
                new AdminMenuAdapter.MenuActionListener() {

                    @Override
                    public void onEdit(MenuItem item) {
                        showMenuDialog(item);
                    }

                    @Override
                    public void onDelete(MenuItem item) {
                        confirmDelete(item);
                    }
                });

        rvAdminMenu.setAdapter(adapter);

        btnAddMenu.setEnabled(false);

        btnAddMenu.setOnClickListener(v ->
                showMenuDialog(null));
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyAdmin();
    }

    // Check Firebase admin account
    private void verifyAdmin() {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            tvEmptyMenu.setText("Please sign in as admin.");
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
                        btnAddMenu.setEnabled(true);
                        loadMenus();

                    } else {
                        tvEmptyMenu.setText("Admin access required.");
                    }
                })
                .addOnFailureListener(e ->
                        tvEmptyMenu.setText(
                                "Unable to verify admin access."));
    }

    // Read menu data from Firebase
    private void loadMenus() {

        if (menuListener != null) {
            menuListener.remove();
        }

        menuListener = db.collection("menus")
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        menuList.clear();
                        adapter.notifyDataSetChanged();

                        tvEmptyMenu.setVisibility(View.VISIBLE);
                        tvEmptyMenu.setText("Failed to load menu.");
                        return;
                    }

                    if (snapshot == null) return;

                    menuList.clear();

                    for (DocumentSnapshot document : snapshot) {

                        MenuItem item =
                                document.toObject(MenuItem.class);

                        if (item != null) {
                            item.setId(document.getId());
                            menuList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    tvEmptyMenu.setText("No menu items yet.");

                    tvEmptyMenu.setVisibility(menuList.isEmpty()
                            ? View.VISIBLE : View.GONE);
                });
    }

    // Add and Edit Menu
    private void showMenuDialog(MenuItem item) {

        if (!adminVerified) return;

        boolean isEdit = item != null;

        ScrollView scrollView = new ScrollView(this);

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (24 *
                getResources().getDisplayMetrics().density);

        form.setPadding(padding, padding, padding, padding);

        EditText etName = new EditText(this);
        etName.setHint("Menu name");

        EditText etCategory = new EditText(this);
        etCategory.setHint("Category");

        EditText etPrice = new EditText(this);
        etPrice.setHint("Price (Rp)");
        etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);

        EditText etDescription = new EditText(this);
        etDescription.setHint("Description");

        EditText etImageUrl = new EditText(this);
        etImageUrl.setHint("Image URL");

        etImageUrl.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_URI);

        form.addView(etName);
        form.addView(etCategory);
        form.addView(etPrice);
        form.addView(etDescription);
        form.addView(etImageUrl);

        scrollView.addView(form);

        if (isEdit) {
            etName.setText(item.getName());
            etCategory.setText(item.getCategory());
            etPrice.setText(String.valueOf(item.getPrice()));
            etDescription.setText(item.getDescription());
            etImageUrl.setText(item.getImageUrl());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isEdit ? "Edit Menu" : "Add New Menu")
                .setView(scrollView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        if (!adminVerified) {
                            dialog.dismiss();
                            return;
                        }

                        String name =
                                etName.getText().toString().trim();

                        String category =
                                etCategory.getText().toString().trim();

                        String priceText =
                                etPrice.getText().toString().trim();

                        String description =
                                etDescription.getText().toString().trim();

                        String imageUrl =
                                etImageUrl.getText().toString().trim();

                        if (name.isEmpty()) {
                            etName.setError("Enter menu name");
                            return;
                        }

                        if (category.isEmpty()) {
                            etCategory.setError("Enter category");
                            return;
                        }

                        long price;

                        try {
                            price = Long.parseLong(priceText);

                            if (price <= 0) {
                                etPrice.setError("Enter a valid price");
                                return;
                            }

                        } catch (NumberFormatException e) {
                            etPrice.setError("Enter a valid price");
                            return;
                        }

                        DocumentReference reference;

                        if (isEdit) {
                            reference = db.collection("menus")
                                    .document(item.getId());
                        } else {
                            reference = db.collection("menus")
                                    .document();
                        }

                        Map<String, Object> data = new HashMap<>();

                        data.put("id", reference.getId());
                        data.put("name", name);
                        data.put("category", category);
                        data.put("price", price);
                        data.put("description", description);
                        data.put("imageUrl", imageUrl);

                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);

                        com.google.android.gms.tasks.Task<Void> task =
                                isEdit ? reference.update(data)
                                        : reference.set(data);

                        task.addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            isEdit ? "Menu updated"
                                                    : "Menu added",
                                            Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                })
                                .addOnFailureListener(e -> {

                                    dialog.getButton(
                                                    AlertDialog.BUTTON_POSITIVE)
                                            .setEnabled(true);

                                    Toast.makeText(this,
                                            "Save failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    });
        });

        dialog.show();
    }

    // Delete Menu
    private void confirmDelete(MenuItem item) {

        if (!adminVerified) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Menu")
                .setMessage("Delete " + item.getName() + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {

                    if (!adminVerified) return;

                    db.collection("menus")
                            .document(item.getId())
                            .delete()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this,
                                            "Menu deleted",
                                            Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this,
                                            "Delete failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show());
                })
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        adminVerified = false;
        btnAddMenu.setEnabled(false);

        if (menuListener != null) {
            menuListener.remove();
            menuListener = null;
        }

        menuList.clear();
        adapter.notifyDataSetChanged();

        tvEmptyMenu.setVisibility(View.VISIBLE);
        tvEmptyMenu.setText("Loading menu...");
    }
}
