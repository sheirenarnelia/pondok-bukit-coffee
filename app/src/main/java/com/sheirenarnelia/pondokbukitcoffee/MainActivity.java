package com.sheirenarnelia.pondokbukitcoffee;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnLogin, btnRegister;
    private TextView tvHeroTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        tvHeroTitle = findViewById(R.id.tvHeroTitle);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Apply Two-Tone Luxury Headline ("Your Cozy Corner" in Espresso, "for Coffee Moments" in Terracotta)
        styleHeadlineText();

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

        // Line 1: Deep Espresso
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
}
