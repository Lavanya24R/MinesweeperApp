package com.example.minesweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.R;
import com.example.minesweeper.firebase.FirebaseManager;
import com.example.minesweeper.model.Player;
import com.example.minesweeper.model.Room;
import com.example.minesweeper.utils.GridGenerator;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class CreateRoomActivity extends AppCompatActivity {
    EditText name;
    ImageButton close, easy, medium, hard;
    int rows, cols, mines;
    boolean joinRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room_activity);
        close=findViewById(R.id.closeBtn);
        easy=findViewById(R.id.easyLevel);
        medium=findViewById(R.id.mediumLevel);
        hard=findViewById(R.id.hardLevel);
        name=findViewById(R.id.name);

        Intent i=getIntent();
        joinRoom=i.getBooleanExtra("joinRoom",false);

        close.setOnClickListener(v -> {
            finish();
        });
        easy.setOnClickListener(v -> {
            openRoom("easy");
        });
        medium.setOnClickListener(v -> {
            openRoom("medium");
        });
        hard.setOnClickListener(v -> {
            openRoom("hard");
        });
    }
    void setLevelConfig(String level)
    {
        switch (level) {
            case "easy":
                rows = 12; cols = 6; mines = 10; break;
            case "medium":
                rows = 21; cols = 8; mines = 35; break;
            case "hard":
                rows = 30; cols = 10; mines = 75; break;
            default:
                rows = 12; cols = 6; mines = 10;
        }
    }
    void openRoom(String level)
    {
        if(joinRoom)
        {
            if(name.getText().toString().trim().isEmpty())
            {
                Toast.makeText(this, "Name not entered", Toast.LENGTH_SHORT).show();
                return;
            }
            String n=name.getText().toString().trim();
            Intent i=new Intent(this, JoinRoomActivity.class);
            i.putExtra("level",level);
            i.putExtra("name",n);
            startActivity(i);
            return;
        }
        else {
            setLevelConfig(level);
            createRoom(level);
        }
    }
    void createRoom(String level) {

        DatabaseReference roomsRef = FirebaseManager.rooms();

        String roomId = roomsRef.push().getKey();

        List<List<Integer>> grid = GridGenerator.generateGrid(rows, cols, mines);

        String userId = "host_" + System.currentTimeMillis();
        String n = name.getText().toString().trim();

        Room room = new Room(grid, rows, cols, mines, userId, n, level);

        room.players.put(userId, new Player(userId, n, false, 0));

        roomsRef.child(roomId).setValue(room)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(CreateRoomActivity.this, "Room Created: " + roomId, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(this, WaitingRoomActivity.class);
                    i.putExtra("userId", userId);
                    i.putExtra("hostName", n);
                    i.putExtra("roomId", roomId);
                    i.putExtra("level", level);
                    startActivity(i);
                });
    }
}
