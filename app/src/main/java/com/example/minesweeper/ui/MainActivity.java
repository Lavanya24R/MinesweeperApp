package com.example.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minesweeper.R;
import com.example.minesweeper.firebase.FirebaseManager;
import com.example.minesweeper.model.Player;
import com.example.minesweeper.model.Room;
import com.example.minesweeper.utils.GridGenerator;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    FrameLayout homeScreen;
    ImageButton arcade, room, stats, easyMode, mediumMode, hardMode, findRoom, createRoom;
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
        homeScreen = findViewById(R.id.main);
        arcade = findViewById(R.id.arcadeBtn);
        room = findViewById(R.id.roomBtn);
        stats = findViewById(R.id.statsBtn);
        levels = findViewById(R.id.levelsOverlay);
        rooms = findViewById(R.id.roomsOverlay);
        easyMode = findViewById(R.id.easyBtn);
        mediumMode = findViewById(R.id.mediumBtn);
        hardMode = findViewById(R.id.harBtn);
        createRoom = findViewById(R.id.createBtn);
        findRoom = findViewById(R.id.findBtn);

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
        easyMode.setOnClickListener(v -> {
            openLevelGrid("easy");
        });
        mediumMode.setOnClickListener(v -> {
            openLevelGrid("medium");
        });
        hardMode.setOnClickListener(v -> {
            openLevelGrid("hard");
        });
        createRoom.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateRoomActivity.class);
            i.putExtra("joinRoom", false);
            startActivity(i);
        });
        findRoom.setOnClickListener(v -> {
            Intent i = new Intent(this, CreateRoomActivity.class);
            i.putExtra("joinRoom", true);
            startActivity(i);
        });
        stats.setOnClickListener(v -> {
            Intent i = new Intent(this, StatsActivity.class);
            startActivity(i);
        });
    }

    void openLevelGrid(String level) {
        Intent i = new Intent(this, GameScreen.class);
        i.putExtra("level", level);
        startActivity(i);
        return;
    }
}