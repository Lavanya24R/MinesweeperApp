package com.example.minesweeper.ui;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minesweeper.R;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class GameScreen extends AppCompatActivity {
    android.os.Handler handler;
    GridLayout gridLayout;
    LinearLayout gameOverOverlay;
    TextView result, numMines, timerText;
    ImageButton restart, close, flag, dig, home;
    boolean[][] isFlagged;
    boolean flagMode = false, win = true, gameEnded = false, timerStarted = false;
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
    SharedPreferences sp;
    private static final String FILE_NAME = "stats";
    private static final String EASY_TIME = "easyTime";
    private static final String MEDIUM_TIME = "mediumTime";
    private static final String HARD_TIME = "hardTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);

        gridLayout = findViewById(R.id.gridLayout);
        gameOverOverlay = findViewById(R.id.overlayBox);
        result = findViewById(R.id.gameOver);
        restart = findViewById(R.id.restartBtn);
        close = findViewById(R.id.closeBtn1);
        numMines = findViewById(R.id.mines);
        flag = findViewById(R.id.flagBtn);
        dig = findViewById(R.id.digBtn);
        home = findViewById(R.id.homeBtn);
        timerText = findViewById(R.id.time);

        sp = getSharedPreferences(FILE_NAME, MODE_PRIVATE);

        level = getIntent().getStringExtra("level");
        setLevelConfig();

        grid = new int[rows][cols];
        cells = new ImageButton[rows][cols];
        isFlagged = new boolean[rows][cols];
        flagsLeft = mines;
        cellsToReveal = rows * cols - mines;

        numMines.setText("🚩" + String.valueOf(flagsLeft));

        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(cols);

        createGrid();
        prepareForFlags();

        handler = new android.os.Handler(android.os.Looper.getMainLooper());

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

        restart.setOnClickListener(v -> {
            handler.removeCallbacks(timerRunnable);
            seconds = 0;
            timerStarted = false;
            recreate();
        });
        close.setOnClickListener(v -> gameOverOverlay.setVisibility(View.GONE));
        home.setOnClickListener(v -> finish());
    }

    void setLevelConfig() {
        switch (level) {
            case "easy":
                rows = 12;
                cols = 6;
                mines = 10;
                break;
            case "medium":
                rows = 21;
                cols = 8;
                mines = 35;
                break;
            case "hard":
                rows = 30;
                cols = 10;
                mines = 75;
                break;
            default:
                rows = 12;
                cols = 6;
                mines = 10;
        }
    }

    void createGrid() {

        gridLayout.post(() -> {

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
        });
    }

    boolean possible(int x, int y) {
        if (x >= 0 && y >= 0 && x < rows && y < cols)
            return true;
        return false;
    }

    void placeMines(int r, int c) {
        Random rand = new Random();
        int count = 0;

        int dx[] = {-1, -1, 0, 1, 1, 1, 0, -1};
        int dy[] = {0, 1, 1, 1, 0, -1, -1, -1};
        while (count < mines) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);

            if (grid[x][y] == -1 || (x == r && y == c) || (x == r - 1 && y == c) || (x == r - 1 && y == c + 1) || (x == r && y == c + 1) || (x == r + 1 && y == c + 1) || (x == r + 1 && y == c) || (x == r + 1 && y == c - 1) || (x == r && y == c - 1) || (x == r - 1 && y == c - 1)) {
                continue;
            } else {
                grid[x][y] = -1;
                count++;
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == -1) continue;
                int cnt = 0;
                for (int k = 0; k < 8; k++) {
                    int nx = i + dx[k];
                    int ny = j + dy[k];
                    if (possible(nx, ny) && grid[nx][ny] == -1)
                        cnt++;
                }
                grid[i][j] = cnt;
            }
        }
    }

    void prepareForFlags() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                isFlagged[i][j] = false;
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

    void reveal(int i, int j) {
        if (!timerStarted) {
            placeMines(i, j);
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
            numMines.setText("🚩" + String.valueOf(flagsLeft));
            return;
        }

        if (isFlagged[i][j]) return;

        if (grid[i][j] == -1) {
            cells[i][j].setImageResource(R.drawable.white_bomb);
            showGameOver(false);
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
            showGameOver(true);
        }
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

    void showGameOver(boolean win) {
        result.setText(win ? "You Win" : "Game Over");
        gameEnded = true;
        handler.removeCallbacks(timerRunnable);

        gameOverOverlay.setVisibility(View.VISIBLE);
        gameOverOverlay.setAlpha(0f);
        gameOverOverlay.animate().alpha(1f).setDuration(300);

        if (win) {
            if (level.equals("easy")) {
                int time = sp.getInt(EASY_TIME, Integer.MAX_VALUE);
                if (seconds < time) {
                    sp.edit().putInt(EASY_TIME, seconds).apply();
                }
            } else if (level.equals("medium")) {
                int time = sp.getInt(MEDIUM_TIME, Integer.MAX_VALUE);
                if (seconds < time) {
                    sp.edit().putInt(MEDIUM_TIME, seconds).apply();
                }
            } else if (level.equals("hard")) {
                int time = sp.getInt(HARD_TIME, Integer.MAX_VALUE);
                if (seconds < time) {
                    sp.edit().putInt(HARD_TIME, seconds).apply();
                }
            }
        }
    }
}
