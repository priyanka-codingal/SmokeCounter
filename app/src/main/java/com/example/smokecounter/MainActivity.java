package com.example.smokecounter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView countText;
    private Button addCigBtn, logoutBtn;

    private SharedPreferences prefs;
    private int cigaretteCount;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countText = findViewById(R.id.countText);
        addCigBtn = findViewById(R.id.addCigaretteBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        prefs = getSharedPreferences("SmokePrefs", Context.MODE_PRIVATE);

        // Get today's date as key
        today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Load count for today
        cigaretteCount = prefs.getInt(today, 0);
        updateCountText();

        addCigBtn.setOnClickListener(v -> {
            cigaretteCount++;
            saveCount();
            updateCountText();
        });

        logoutBtn.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            finish();
        });
    }

    private void saveCount() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(today, cigaretteCount); // Save count for today only
        editor.apply();
    }

    private void updateCountText() {
        countText.setText("Cigarettes smoked today: " + cigaretteCount);
    }
}
