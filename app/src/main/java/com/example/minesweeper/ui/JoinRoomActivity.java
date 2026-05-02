package com.example.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.R;
import com.example.minesweeper.firebase.FirebaseManager;
import com.example.minesweeper.model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class JoinRoomActivity extends AppCompatActivity {
    LinearLayout roomContainer;
    LayoutInflater inflater;
    EditText roomId;
    ImageButton joinBtn;
    TextView levelText;
    String level,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_room_activity);

        roomId=findViewById(R.id.roomId);
        joinBtn=findViewById(R.id.joinBtn);
        levelText=findViewById(R.id.level);

        joinBtn.setOnClickListener(v -> {
            joinRoomById();
        });

        roomContainer = findViewById(R.id.roomContainer);
        inflater = LayoutInflater.from(this);

        Intent i=getIntent();
        level=i.getStringExtra("level");
        name=i.getStringExtra("name");

        levelText.setText("Level: "+level);

        loadRooms(level);

    }
    void loadRooms(String selectedDifficulty) {
        cleanupInactiveRooms();
        DatabaseReference roomsRef = FirebaseManager.rooms();

        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                roomContainer.removeAllViews();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    String status = roomSnap.child("status").getValue(String.class);
                    if (status == null || !status.equalsIgnoreCase("waiting")) continue;

                    String currentRoomId = roomSnap.getKey();

                    String difficulty = roomSnap.child("difficulty").getValue(String.class);

                    if (difficulty == null || !difficulty.equals(selectedDifficulty))
                        continue;

                    String hostId = roomSnap.child("hostId").getValue(String.class);
                    String hostName = roomSnap.child("hostName").getValue(String.class);

                    int playerCount = (int) roomSnap.child("players").getChildrenCount();

                    View view = inflater.inflate(R.layout.rooms_card, roomContainer, false);

                    TextView hostText = view.findViewById(R.id.hostText);
                    TextView playerCountText = view.findViewById(R.id.playerCount);

                    hostText.setText("Host: " + (hostName != null ? hostName : "Unknown"));
                    playerCountText.setText("Players: " + String.valueOf(playerCount));

                    view.setOnClickListener(v -> joinRoom(currentRoomId, hostName));

                    roomContainer.addView(view);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    void joinRoom(String roomId, String host) {

        if (roomId.isEmpty()) {
            Toast.makeText(this, "Unexpected Error Occurred", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference roomRef = FirebaseManager.rooms().child(roomId);

        roomRef.get().addOnSuccessListener(snapshot -> {

            if (!snapshot.exists()) {
                Toast.makeText(this, "Room not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = "player" + System.currentTimeMillis();

            roomRef.child("players")
                    .child(userId)
                    .setValue(new Player(userId,name, false, 0, -1));


            Intent i = new Intent(this, WaitingRoomActivity.class);
            i.putExtra("roomId", roomId);
            i.putExtra("userId", userId);
            i.putExtra("level", level);
            i.putExtra("hostName", host);
            startActivity(i);

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
    void joinRoomById() {
        String id = roomId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Enter Room ID", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference roomRef = FirebaseManager.rooms().child(id);

        roomRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Toast.makeText(this, "Room not found", Toast.LENGTH_SHORT).show();
                return;
            }
            String status = snapshot.child("status").getValue(String.class);
            if (!"waiting".equals(status))
            {
                Toast.makeText(this, "Game already started", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = "player" + System.currentTimeMillis();
            roomRef.child("players")
                    .child(userId)
                    .setValue(new Player(userId, name, false, 0, -1));

            String hostName = snapshot.child("hostName").getValue(String.class);
            if (hostName == null) hostName = "Unknown";

            roomRef.child("lastActive").setValue(System.currentTimeMillis());

            Intent i = new Intent(this, WaitingRoomActivity.class);
            i.putExtra("roomId", id);
            i.putExtra("userId", userId);
            i.putExtra("level", level);
            i.putExtra("hostName", hostName);
            startActivity(i);

        });
    }
    void cleanupInactiveRooms() {

        DatabaseReference roomsRef = FirebaseManager.rooms();

        long currentTime = System.currentTimeMillis();
        long TIME_LIMIT = 2 * 60 * 1000;

        roomsRef.get().addOnSuccessListener(snapshot -> {

            for (DataSnapshot roomSnap : snapshot.getChildren()) {

                Long lastActive = roomSnap.child("lastActive").getValue(Long.class);
                long playerCount = roomSnap.child("players").getChildrenCount();

                if (playerCount == 0 ||
                        (lastActive != null && currentTime - lastActive > TIME_LIMIT)) {

                    roomSnap.getRef().removeValue();
                }
            }
        });
    }
}