package com.example.minesweeper;

import android.content.Intent;
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
    ImageButton arcade, room, leaderBoard, easyMode, mediumMode, hardMode, findRoom, createRoom;
    LinearLayout levels, rooms;

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
        arcade=findViewById(R.id.arcadeBtn);
        room=findViewById(R.id.roomBtn);
        leaderBoard=findViewById(R.id.leaderBoardBtn);
        levels = findViewById(R.id.levelsOverlay);
        rooms = findViewById(R.id.roomsOverlay);
        easyMode = findViewById(R.id.easyBtn);
        mediumMode = findViewById(R.id.mediumBtn);
        hardMode = findViewById(R.id.harBtn);

        ImageButton close1 = findViewById(R.id.closeBtn1);
        ImageButton close2 = findViewById(R.id.closeBtn2);

        close1.setOnClickListener(v -> {
            levels.setVisibility(View.GONE);
        });
        close2.setOnClickListener(v -> {
            rooms.setVisibility(View.GONE);
        });
        arcade.setOnClickListener(v -> {
            levels.setVisibility(View.VISIBLE);
            levels.setAlpha(0f);
            levels.animate().alpha(1f).setDuration(300);
        });
        room.setOnClickListener(v -> {
            rooms.setVisibility(View.VISIBLE);
            rooms.setAlpha(0f);
            rooms.animate().alpha(1f).setDuration(300);
        });
        leaderBoard.setOnClickListener(v -> {
            //leader board code
        });
        easyMode.setOnClickListener(v -> {
            openLevelGrid("easy");
        });
        mediumMode.setOnClickListener(v -> {
            openLevelGrid("medium");
        });
        hardMode.setOnClickListener(v -> {
            openLevelGrid("hard");
        });
    }
    void openLevelGrid(String level)
    {
        Intent i=new Intent(this,GameScreen.class);
        i.putExtra("level",level);
        startActivity(i);
    }
}