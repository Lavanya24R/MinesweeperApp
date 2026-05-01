package com.example.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.R;
import com.example.minesweeper.firebase.FirebaseManager;
import com.example.minesweeper.model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class WaitingRoomActivity extends AppCompatActivity {
    LinearLayout playerContainer;
    LayoutInflater inflater;
    String userId, hostName, roomId, level;
    TextView host, levelText, roomIdText;
    ImageButton startBtn;
    FrameLayout startLayout;
    boolean isHost, gameStarted = false;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_room_activity);

        Intent i = getIntent();
        userId = i.getStringExtra("userId");
        hostName = i.getStringExtra("hostName");
        roomId = i.getStringExtra("roomId");
        level = i.getStringExtra("level");

        host = findViewById(R.id.host);
        levelText = findViewById(R.id.level);
        roomIdText = findViewById(R.id.roomId);
        startBtn = findViewById(R.id.startBtn);
        startLayout = findViewById(R.id.startLayout);

        playerContainer = findViewById(R.id.playerContainer);
        inflater = LayoutInflater.from(this);

        playerContainer.removeAllViews();

        host.setText("Host: " + hostName);
        levelText.setText("Level: " + level);
        roomIdText.setText("Room ID: " + roomId);

        loadPlayers();

        startBtn.setOnClickListener(v -> {
            FirebaseManager.rooms()
                    .child(roomId)
                    .child("status")
                    .setValue("playing");
        });

    }
    void loadPlayers() {

        DatabaseReference roomRef = FirebaseManager.rooms().child(roomId);

        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                playerContainer.removeAllViews();

                String hostId = snapshot.child("hostId").getValue(String.class);
                isHost = userId.equals(hostId);

                for (DataSnapshot playerSnap : snapshot.child("players").getChildren()) {
                    Player player = playerSnap.getValue(Player.class);
                    if (player == null) continue;

                    View view = inflater.inflate(R.layout.player_card, playerContainer, false);
                    TextView nameText = view.findViewById(R.id.playerName);

                    String display = player.name != null ? player.name : "Player";

                    if (playerSnap.getKey().equals(userId)) {
                        display += " (You)";
                    }

                    if (playerSnap.getKey().equals(hostId)) {
                        display += " 👑";
                    }

                    nameText.setText(display);

                    playerContainer.addView(view);
                }
                startLayout.setVisibility(isHost ? View.VISIBLE : View.GONE);
                String status = snapshot.child("status").getValue(String.class);

                if ("playing".equals(status) && !gameStarted) {
                    gameStarted = true;
                    startGame();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
    void startGame() {

        Intent in = new Intent(this, SharedGameScreen.class);
        in.putExtra("userId", userId);
        in.putExtra("hostName", hostName);
        in.putExtra("roomId", roomId);
        in.putExtra("level", level);

        startActivity(in);
        finish();
    }
}
