package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vpHeroSlider;
    private LinearLayout llDotsIndicator;
    private MaterialButton btnLogin, btnRegister;

    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;
    private List<HeroSlide> slides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        vpHeroSlider = findViewById(R.id.vpHeroSlider);
        llDotsIndicator = findViewById(R.id.llDotsIndicator);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Setup Pondok Bukit Real Photos Slider
        setupHeroSlider();

        // Navigate to Login Screen via Intent
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Navigate to Register Screen via Intent
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void setupHeroSlider() {
        slides = new ArrayList<>();
        slides.add(new HeroSlide(
                R.drawable.img_pondok_garden,
                "LUSH GARDEN & SAUNG",
                "Tranquil Hillside Saung",
                "Sip handcrafted coffee in traditional gazebos surrounded by cooling mountain breeze."
        ));
        slides.add(new HeroSlide(
                R.drawable.img_pondok_deck,
                "EVENING AMBIANCE",
                "Warm Glowing Lights",
                "Magical evenings under fairy lights, made for relaxing coffee chats and cozy moments."
        ));
        slides.add(new HeroSlide(
                R.drawable.img_pondok_outdoor,
                "PANORAMIC TERRACE",
                "Scenic Deck Seating",
                "Open-air wooden terrace overlooking panoramic valley and lush mountain scenery."
        ));
        slides.add(new HeroSlide(
                R.drawable.img_pondok_view,
                "MOUNTAIN ESCAPE",
                "Pure Nature Retreat",
                "Your peaceful hilltop haven to unwind, savor fresh brews, and breathe easy."
        ));

        HeroSliderAdapter adapter = new HeroSliderAdapter(slides);
        vpHeroSlider.setAdapter(adapter);

        setupDotsIndicator(slides.size());
        setCurrentDot(0);

        vpHeroSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentDot(position);
                // Reset timer when user swipes manually
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 4000);
            }
        });

        // Auto-scroll every 4 seconds
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int nextIndex = (vpHeroSlider.getCurrentItem() + 1) % slides.size();
                vpHeroSlider.setCurrentItem(nextIndex, true);
                sliderHandler.postDelayed(this, 4000);
            }
        };
    }

    private void setupDotsIndicator(int count) {
        llDotsIndicator.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(6, 0, 6, 0);

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(getDrawable(R.drawable.dot_inactive));
            dot.setLayoutParams(params);
            llDotsIndicator.addView(dot);
        }
    }

    private void setCurrentDot(int index) {
        int childCount = llDotsIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView dot = (ImageView) llDotsIndicator.getChildAt(i);
            if (i == index) {
                dot.setImageDrawable(getDrawable(R.drawable.dot_active));
            } else {
                dot.setImageDrawable(getDrawable(R.drawable.dot_inactive));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 4000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }
}
