package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 vpHeroSlider;
    private LinearLayout llDotsIndicator;
    private TextView tvHeroTitle;
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
        tvHeroTitle = findViewById(R.id.tvHeroTitle);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Apply Two-Tone Luxury Headline
        styleHeadlineText();

        // Setup Fore Coffee-Style Hero Carousel with 3 Real Photos & Value Propositions
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

    private void styleHeadlineText() {
        String line1 = "Your Cozy Corner\n";
        String line2 = "for Coffee Moments";
        String fullText = line1 + line2;

        SpannableStringBuilder ssb = new SpannableStringBuilder(fullText);

        int espressoColor = ContextCompat.getColor(this, R.color.brand_espresso);
        int terracottaColor = ContextCompat.getColor(this, R.color.brand_terracotta);

        // Line 1: Deep Roast Espresso
        ssb.setSpan(
                new ForegroundColorSpan(espressoColor),
                0,
                line1.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Line 2: Warm Terracotta Accent
        ssb.setSpan(
                new ForegroundColorSpan(terracottaColor),
                line1.length(),
                fullText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvHeroTitle.setText(ssb);
    }

    private void setupHeroSlider() {
        slides = new ArrayList<>();
        slides.add(new HeroSlide(
                R.drawable.img_pondok_outdoor,
                "PANORAMIC TERRACE",
                "Artisanal Brews, Hillside Views",
                "Panoramic valley deck seating for your morning and evening coffee."
        ));
        slides.add(new HeroSlide(
                R.drawable.img_pondok_garden,
                "COZY NATURE RETREAT",
                "Tranquil Saung & Garden",
                "Private traditional gazebos surrounded by cooling mountain breeze."
        ));
        slides.add(new HeroSlide(
                R.drawable.img_pondok_view,
                "SEAMLESS ORDERING",
                "Order Ahead, Skip the Wait",
                "Dine-in at your favorite spot or quick pickup via QRIS & Cashier."
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
