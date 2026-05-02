package com.example.minesweeper.model;

public class Player {
    public String name;
    public String userId;
    public boolean finished;
    public int time;
    public int rank;

    public Player() {}

    public Player(String userId, String name, boolean finished, int time, int rank) {
        this.userId = userId;
        this.name=name;
        this.finished = finished;
        this.time = time;
        this.rank = rank;
    }
}