
package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MenuListActivity extends AppCompatActivity {

    private RecyclerView rvMenuList;
    private MenuAdapter menuAdapter;
    private List<MenuItem> allMenuList = new ArrayList<>();
    private List<MenuItem> filteredMenuList = new ArrayList<>();

    private ChipGroup chipGroupCategory;
    private EditText etSearch;
    private TextView tvItemCount;
    private ProgressBar progressBar;
    private View btnCart;

    // Order History (Tugas Orang 3)
    private View btnOrderHistory;

    private FirebaseFirestore db;
    private String currentCategory = "All";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        initViews();
        setupRecyclerView();
        setupFilters();

        db = FirebaseFirestore.getInstance();

        // 1. Show local menu catalog immediately so the screen never gets stuck loading
        allMenuList.clear();
        allMenuList.addAll(FirebaseSeeder.createDefaultMenuList());
        applyFilters();
        progressBar.setVisibility(View.GONE);

        // 2. Sync & seed with Firestore in background
        FirebaseSeeder.seedMenuDataIfEmpty(db, this::loadMenusFromFirestore);
    }

    private void initViews() {
        rvMenuList = findViewById(R.id.rvMenuList);
        chipGroupCategory = findViewById(R.id.chipGroupCategory);
        etSearch = findViewById(R.id.etSearch);
        tvItemCount = findViewById(R.id.tvItemCount);
        progressBar = findViewById(R.id.progressBar);
        btnCart = findViewById(R.id.btnCart);

        // Order History Navigation (Tugas Orang 3)
        btnOrderHistory = findViewById(R.id.btnOrderHistory);

        btnOrderHistory.setOnClickListener(v -> {
            startActivity(new Intent(
                    MenuListActivity.this,
                    OrderHistoryActivity.class));
        });

        btnCart.setOnClickListener(v -> {
            try {
                // Routing to CartActivity (Orang 1)
                Class<?> cartClass = Class.forName("com.sheirenarnelia.pondokbukitcoffee.CartActivity");
                startActivity(new Intent(this, cartClass));
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "Cart page is being finalized by teammate!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        menuAdapter = new MenuAdapter(this, filteredMenuList);
        rvMenuList.setLayoutManager(new GridLayoutManager(this, 2));
        rvMenuList.setAdapter(menuAdapter);

        menuAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(MenuListActivity.this, DetailMenuActivity.class);
            intent.putExtra("menu_item", item);
            startActivity(intent);
        });
    }

    private void setupFilters() {
        // Category Filter
        chipGroupCategory.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int selectedId = checkedIds.get(0);

            if (selectedId == R.id.chipAll) {
                currentCategory = "All";
            } else if (selectedId == R.id.chipCoffee) {
                currentCategory = "Coffee";
            } else if (selectedId == R.id.chipNonCoffee) {
                currentCategory = "Non-Coffee";
            } else if (selectedId == R.id.chipMainCourse) {
                currentCategory = "Main Course";
            } else if (selectedId == R.id.chipSnacks) {
                currentCategory = "Snacks";
            }

            applyFilters();
        });

        // Search Filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadMenusFromFirestore() {
        db.collection("menus")
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        // Keep displaying local catalog silently if Firestore fails/offline
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        allMenuList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            MenuItem item = doc.toObject(MenuItem.class);
                            if (item != null) {
                                if (item.getId() == null) {
                                    item.setId(doc.getId());
                                }
                                allMenuList.add(item);
                            }
                        }
                        applyFilters();
                    }
                });
    }

    private void applyFilters() {
        filteredMenuList.clear();

        for (MenuItem item : allMenuList) {
            boolean matchesCategory = currentCategory.equals("All") ||
                    (item.getCategory() != null && item.getCategory().equalsIgnoreCase(currentCategory));

            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    (item.getName() != null && item.getName().toLowerCase().contains(currentSearchQuery)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(currentSearchQuery));

            if (matchesCategory && matchesSearch) {
                filteredMenuList.add(item);
            }
        }

        menuAdapter.updateList(filteredMenuList);
        tvItemCount.setText(filteredMenuList.size() + " items");
    }
}
