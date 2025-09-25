package com.example.smokecounter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText, countText;
    private Button addCigaretteBtn, monthlyLogBtn, logoutBtn;

    private SmokeManager smokeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        countText = findViewById(R.id.countText);
        addCigaretteBtn = findViewById(R.id.addCigaretteBtn);
        monthlyLogBtn = findViewById(R.id.monthlyLogBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        smokeManager = SmokeManager.getInstance(this);

        // Display user name
        String user = getIntent().getStringExtra("userIdentifier");
        welcomeText.setText("Welcome " + (user != null ? user : "User"));

        // Show current daily count
        countText.setText("Cigarettes today: " + smokeManager.getDailyCount());

        addCigaretteBtn.setOnClickListener(v -> {
            smokeManager.incrementCount();
            countText.setText("Cigarettes today: " + smokeManager.getDailyCount());
        });

        monthlyLogBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MonthlyLogActivity.class));
        });

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        scheduleDailyReset();
    }

    private void scheduleDailyReset() {
        PeriodicWorkRequest resetWorkRequest =
                new PeriodicWorkRequest.Builder(DailyResetWorker.class, 24, TimeUnit.HOURS)
                        .build();
        WorkManager.getInstance(this).enqueue(resetWorkRequest);
    }
}