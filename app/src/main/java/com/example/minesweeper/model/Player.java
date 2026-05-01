package com.example.minesweeper.model;

public class Player {
    public String name;
    public String userId;
    public boolean finished;
    public int time;

    public Player() {}

    public Player(String userId, String name, boolean finished, int time) {
        this.userId = userId;
        this.name=name;
        this.finished = finished;
        this.time = time;
    }
}