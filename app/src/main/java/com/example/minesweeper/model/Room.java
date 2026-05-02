package com.example.minesweeper.model;

import java.util.HashMap;
import java.util.List;

public class Room {
    public List<List<Integer>> grid;
    public int rows, cols, mines;
    public String difficulty;
    public String status;
    public long lastActive;
    public HashMap<String, Player> players;
    public String hostName;
    public String hostId;

    public Room() {}

    public Room(List<List<Integer>> grid, int rows, int cols, int mines, String hostId, String name, String difficulty, long lastActive) {
        this.grid = grid;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.status = "waiting";
        this.hostId = hostId;
        this.hostName = name;
        this.lastActive = lastActive;
        this.players = new HashMap<>();
        this.difficulty=difficulty;
    }

}