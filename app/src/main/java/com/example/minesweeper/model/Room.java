package com.example.minesweeper.model;

import java.util.HashMap;
import java.util.List;

public class Room {
    public List<List<Integer>> grid;
    public int rows, cols, mines;
    public String difficulty;
    public String status;
    public HashMap<String, Player> players;
    public String hostName;
    public String hostId;

    public Room() {}

    public Room(List<List<Integer>> grid, int rows, int cols, int mines, String hostId, String name, String difficulty) {
        this.grid = grid;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.status = "waiting";
        this.hostId = hostId;
        this.hostName = name;
        this.players = new HashMap<>();
        this.difficulty=difficulty;
    }
    public void addPlayer(String id, Player player) {
        players.put(id, player);
    }

}