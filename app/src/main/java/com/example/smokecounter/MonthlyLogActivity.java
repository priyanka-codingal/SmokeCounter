package com.example.smokecounter;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MonthlyLogActivity extends AppCompatActivity {

    private SmokeManager smokeManager;
    private TextView monthlyLogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_log);

        monthlyLogText = findViewById(R.id.monthlyLogText);

        // Singleton
        smokeManager = SmokeManager.getInstance(this);

        int monthlyTotal = smokeManager.getMonthlyTotal();
        monthlyLogText.setText("Total cigarettes this month: " + monthlyTotal);
    }
}
