package com.example.minesweeper.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.R;
import com.example.minesweeper.firebase.FirebaseManager;
import com.example.minesweeper.model.Player;
import com.google.firebase.database.*;

import java.util.*;

public class SharedGameScreen extends AppCompatActivity {
    ValueEventListener leaderboardListener;
    DatabaseReference playersRef;
    android.os.Handler handler;

    String userId, roomId;

    long startTime;
    boolean finished = false;

    LinearLayout leaderboardContainer;
    GridLayout gridLayout;
    View overlayBox;
    LayoutInflater inflater;
    TextView result, numMines, timerText;
    ImageButton closeBtn, flag, dig, home, leaderBoardBtn;
    boolean[][] isFlagged;
    boolean flagMode = false, win = true, gameEnded = false, timerStarted = false, leaderboardShown = false, winnerToastShown = false;
    ;
    String level;
    int rows, cols, mines, flagsLeft, cellsToReveal, seconds = 0;
    int[][] grid;
    int[] numbers = {
            R.drawable.number_1,
            R.drawable.number_2,
            R.drawable.number_3,
            R.drawable.number_4,
            R.drawable.number_5,
            R.drawable.number_6,
            R.drawable.number_7,
            R.drawable.number_8
    };
    ColorStateList[] numColours = {
            ColorStateList.valueOf(Color.parseColor("#2962FF")),
            ColorStateList.valueOf(Color.parseColor("#2E7D32")),
            ColorStateList.valueOf(Color.parseColor("#FF3636")),
            ColorStateList.valueOf(Color.parseColor("#283593")),
            ColorStateList.valueOf(Color.parseColor("#691C02")),
            ColorStateList.valueOf(Color.parseColor("#00838F")),
            ColorStateList.valueOf(Color.BLACK),
            ColorStateList.valueOf(Color.GRAY)
    };
    ImageButton[][] cells;
    List<Player> cachedPlayers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shared_game_activity);

        userId = getIntent().getStringExtra("userId");
        roomId = getIntent().getStringExtra("roomId");

        overlayBox = findViewById(R.id.overlayBox);
        leaderboardContainer = findViewById(R.id.leaderboardContainer);
        closeBtn = findViewById(R.id.closeBtn);
        gridLayout = findViewById(R.id.gridLayout);
        numMines = findViewById(R.id.mines);
        flag = findViewById(R.id.flagBtn);
        dig = findViewById(R.id.digBtn);
        home = findViewById(R.id.homeBtn);
        timerText = findViewById(R.id.time);
        leaderBoardBtn = findViewById(R.id.leaderBoardBtn);

        prepareGame();

        handler = new android.os.Handler(android.os.Looper.getMainLooper());

        listenForResults();

        flag.setOnClickListener(v -> {
            flag.setImageTintList(ColorStateList.valueOf(Color.BLACK));
            dig.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            flagMode = true;
        });

        dig.setOnClickListener(v -> {
            dig.setImageTintList(ColorStateList.valueOf(Color.BLACK));
            flag.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            flagMode = false;
        });

        home.setOnClickListener(v ->  {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
        });

        inflater = LayoutInflater.from(this);

        closeBtn.setOnClickListener(v -> overlayBox.setVisibility(View.GONE));

        leaderBoardBtn.setOnClickListener(v -> {
            if (cachedPlayers.size() == 0) {
                Toast.makeText(this, "No results yet", Toast.LENGTH_SHORT).show();
                return;
            }
            showLeaderBoard(cachedPlayers);
        });

    }

    void prepareGame() {

        DatabaseReference roomRef =
                FirebaseManager.rooms().child(roomId);

        roomRef.get().addOnSuccessListener(snapshot -> {

            if (!snapshot.exists()) return;

            Long r = snapshot.child("rows").getValue(Long.class);
            Long c = snapshot.child("cols").getValue(Long.class);
            Long m = snapshot.child("mines").getValue(Long.class);

            if (r != null) rows = r.intValue();
            if (c != null) cols = c.intValue();
            if (m != null) mines = m.intValue();

            grid = new int[rows][cols];
            cells = new ImageButton[rows][cols];

            List<List<Long>> firebaseGrid =
                    (List<List<Long>>) snapshot.child("grid").getValue();

            if (firebaseGrid == null || firebaseGrid.size() < rows) return;

            for (int i = 0; i < rows; i++) {
                List<Long> row = firebaseGrid.get(i);

                for (int j = 0; j < cols; j++) {
                    if (row == null || row.size() <= j) continue;
                    grid[i][j] = row.get(j).intValue();
                }
            }

            isFlagged = new boolean[rows][cols];
            flagsLeft = mines;
            cellsToReveal = rows * cols - mines;

            numMines.setText("🚩" + flagsLeft);

            gridLayout.setRowCount(rows);
            gridLayout.setColumnCount(cols);

            createGrid();

            gridLayout.post(() -> openFirstFewCells());

            prepareForFlags();

        }).addOnFailureListener(e -> {
            Log.e("ERROR", e.getMessage());
        });
    }

    void prepareForFlags() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                isFlagged[i][j] = false;
            }
        }
    }

    void createGrid() {

        int size = getResources().getDisplayMetrics().widthPixels / cols;

        gridLayout.removeAllViews();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                ImageButton btn = new ImageButton(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = size;
                params.height = size;

                btn.setLayoutParams(params);
                btn.setBackgroundResource(R.drawable.tile);
                btn.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
                btn.setPadding(20, 20, 20, 20);

                int x = i, y = j;

                btn.setOnClickListener(v -> reveal(x, y));

                cells[i][j] = btn;
                gridLayout.addView(btn);
            }
        }
    }

    void reveal(int i, int j) {

        DatabaseReference roomRef = FirebaseManager.rooms().child(roomId);
        roomRef.child("lastActive").setValue(System.currentTimeMillis());

        if (!timerStarted) {
            timerStarted = true;
            handler.postDelayed(timerRunnable, 1000);
        }

        if (gameEnded) return;
        if (!cells[i][j].isEnabled()) return;

        if (flagMode) {
            if (!isFlagged[i][j] && flagsLeft > 0) {
                cells[i][j].setImageResource(R.drawable.white_flag);
                isFlagged[i][j] = true;
                flagsLeft--;
            } else if (isFlagged[i][j]) {
                cells[i][j].setImageDrawable(null);
                isFlagged[i][j] = false;
                flagsLeft++;
            }
            numMines.setText("🚩" + flagsLeft);
            return;
        }

        if (isFlagged[i][j]) return;

        if (grid[i][j] == -1) {
            Toast.makeText(this, "Penalty: +20sec", Toast.LENGTH_SHORT).show();
            cells[i][j].setImageResource(R.drawable.white_bomb);
            addPenaltyTime();
            return;
        }

        int count = grid[i][j];

        if (count == 0) {
            cells[i][j].setImageDrawable(null);
            cells[i][j].setBackground(null);
            floodFill(i, j);
        }
        else {
            cells[i][j].setBackground(null);
            cells[i][j].setImageResource(numbers[count - 1]);
            cells[i][j].setImageTintList(numColours[count - 1]);
            cells[i][j].setScaleType(ImageButton.ScaleType.CENTER_INSIDE);

            if (cells[i][j].isEnabled()) {
                cells[i][j].setEnabled(false);
                cellsToReveal--;
            }
        }

        if (cellsToReveal == 0) {
            onGameFinished();
        }
    }

    void openFirstFewCells() {
        if (cells == null || grid == null) return;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0) {
                    floodFill(i, j);
                    return;
                }
            }
        }
    }

    boolean possible(int x, int y) {
        if (x >= 0 && y >= 0 && x < rows && y < cols)
            return true;
        return false;
    }

    void floodFill(int startX, int startY) {

        int dx[] = {-1, -1, 0, 1, 1, 1, 0, -1};
        int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};

        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});

        while (!queue.isEmpty()) {

            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            if (cells[x][y] == null || !cells[x][y].isEnabled()) continue;

            int count = grid[x][y];

            if (count == 0) {
                cells[x][y].setImageDrawable(null);
                cells[x][y].setBackground(null);
            } else {
                cells[x][y].setBackground(null);
                cells[x][y].setImageResource(numbers[count - 1]);
                cells[x][y].setImageTintList(numColours[count - 1]);
            }

            cells[x][y].setEnabled(false);
            cellsToReveal--;

            if (count == 0) {
                for (int k = 0; k < 8; k++) {
                    int nx = x + dx[k];
                    int ny = y + dy[k];

                    if (possible(nx, ny) &&
                            cells[nx][ny] != null &&
                            cells[nx][ny].isEnabled() &&
                            !isFlagged[nx][ny]) {

                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            seconds++;

            int mins = seconds / 60;
            int secs = seconds % 60;

            String time = String.format("%02d:%02d", mins, secs);
            timerText.setText(time);

            handler.postDelayed(this, 1000);
        }
    };

    void addPenaltyTime() {
        seconds += 20;
    }

    public void onGameFinished() {
        gameEnded = true;
        if (finished) return;
        finished = true;

        int timeTaken = seconds;

        handler.removeCallbacks(timerRunnable);

        DatabaseReference playersRef =
                FirebaseManager.rooms().child(roomId).child("players");

        playersRef.child(userId).child("finished").setValue(true);
        playersRef.child(userId).child("time").setValue(timeTaken);

        assignRank(playersRef);
    }

    void assignRank(DatabaseReference playersRef) {

        playersRef.get().addOnSuccessListener(snapshot -> {

            int rank = 1;

            for (DataSnapshot snap : snapshot.getChildren()) {
                Player p = snap.getValue(Player.class);

                if (p != null && p.finished && !snap.getKey().equals(userId)) {
                    rank++;
                }
            }

            playersRef.child(userId).child("rank").setValue(rank);
        });
    }

    void listenForResults() {

        playersRef = FirebaseManager.rooms()
                .child(roomId)
                .child("players");

        leaderboardListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<Player> finishedPlayers = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Player p = snap.getValue(Player.class);

                    if (p != null && p.finished) {
                        finishedPlayers.add(p);
                    }
                }

                if (finishedPlayers.size() == 0) return;

                Collections.sort(finishedPlayers, (a, b) -> a.rank - b.rank);

                cachedPlayers = finishedPlayers;

                Player current = snapshot.child(userId).getValue(Player.class);

                if (current != null && current.finished && !leaderboardShown) {
                    leaderboardShown = true;
                    showLeaderBoard(cachedPlayers);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        };

        playersRef.addValueEventListener(leaderboardListener);
    }

    void showLeaderBoard(List<Player> players) {

        overlayBox.setVisibility(View.VISIBLE);
        leaderboardContainer.removeAllViews();

        if (players.size() > 0 &&
                players.get(0).userId.equals(userId) &&
                !winnerToastShown) {

            winnerToastShown = true;
            Toast.makeText(this, "🏆 You Won!", Toast.LENGTH_SHORT).show();
        }

        for (Player p : players) {

            View item = inflater.inflate(R.layout.leaderboard_item, leaderboardContainer, false);
            TextView details = item.findViewById(R.id.details);

            String name = p.name;
            if (p.userId.equals(userId)) {
                name += " (You)";
                details.setTextColor(Color.YELLOW);
            }
            int mins = p.time / 60;
            int secs = p.time % 60;

            String time = String.format("%02d:%02d", mins, secs);
            String text = "#" + p.rank + " " + name + " - " + time;

            details.setText(text);
            leaderboardContainer.addView(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
        if (playersRef != null && leaderboardListener != null) {
            playersRef.removeEventListener(leaderboardListener);
        }
    }
}