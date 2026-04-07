package com.example.minesweeper;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    FrameLayout homeScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        homeScreen=findViewById(R.id.main);
        ImageButton arcade=findViewById(R.id.arcadeBtn);
        ImageButton room=findViewById(R.id.roomBtn);
        ImageButton leaderBoard=findViewById(R.id.leaderBoardBtn);
        LinearLayout levels = findViewById(R.id.levelsOverlay);
        LinearLayout rooms = findViewById(R.id.roomsOverlay);

        ImageButton close1 = findViewById(R.id.closeBtn1);
        ImageButton close2 = findViewById(R.id.closeBtn2);

        close1.setOnClickListener(v -> {
            levels.setVisibility(View.GONE);
            homeScreen.setPadding(30,30,30,30);
        });
        close2.setOnClickListener(v -> {
            rooms.setVisibility(View.GONE);
            homeScreen.setPadding(30,30,30,30);
        });
        arcade.setOnClickListener(v -> {
            homeScreen.setPadding(0,0,0,0);
            levels.setVisibility(View.VISIBLE);
            levels.setAlpha(0f);
            levels.animate().alpha(1f).setDuration(300);
        });
        room.setOnClickListener(v -> {
            homeScreen.setPadding(0,0,0,0);
            rooms.setVisibility(View.VISIBLE);
            rooms.setAlpha(0f);
            rooms.animate().alpha(1f).setDuration(300);
        });
        leaderBoard.setOnClickListener(v -> {
            homeScreen.setPadding(0,0,0,0);
        });
    }
}