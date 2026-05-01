package com.example.minesweeper.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.R;

public class StatsActivity extends AppCompatActivity {
    private static final String FILE_NAME = "stats";
    private static final String EASY_TIME = "easyTime";
    private static final String MEDIUM_TIME = "mediumTime";
    private static final String HARD_TIME = "hardTime";
    TextView easyTime, mediumTime, hardTime;
    ImageButton homeBtn;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_activity);

        easyTime = findViewById(R.id.easyTime);
        mediumTime = findViewById(R.id.mediumTime);
        hardTime = findViewById(R.id.hardTime);
        homeBtn = findViewById(R.id.homeBtn);

        sp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        int easy = sp.getInt(EASY_TIME, -1);
        int medium = sp.getInt(MEDIUM_TIME, -1);
        int hard = sp.getInt(HARD_TIME, -1);

        if (easy != -1) {
            int mins = easy / 60;
            int secs = easy % 60;
            String time = String.format("%02d:%02d", mins, secs);
            easyTime.setText(time);
        }
        else {
            easyTime.setText("NaN");
        }

        if (medium != -1) {
            int mins = medium / 60;
            int secs = medium % 60;
            String time = String.format("%02d:%02d", mins, secs);
            mediumTime.setText(time);
        }
        else {
            mediumTime.setText("NaN");
        }

        if (hard != -1) {
            int mins = hard / 60;
            int secs = hard % 60;
            String time = String.format("%02d:%02d", mins, secs);
            hardTime.setText(time);
        }
        else {
            hardTime.setText("NaN");
        }
        homeBtn.setOnClickListener(v -> finish());
    }

}
