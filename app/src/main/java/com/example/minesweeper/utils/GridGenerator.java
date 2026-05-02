package com.example.minesweeper.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridGenerator {

    public static List<List<Integer>> generateGrid(int rows, int cols, int mines) {
        int[][] grid = new int[rows][cols];
        Random rand = new Random();

        int count = 0;
        while (count < mines) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);

            if (grid[x][y] != -1) {
                grid[x][y] = -1;
                count++;
            }
        }

        int dx[] = {-1,-1,0,1,1,1,0,-1};
        int dy[] = {0,1,1,1,0,-1,-1,-1};

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == -1) continue;

                int cnt = 0;
                for (int k = 0; k < 8; k++) {
                    int nx = i + dx[k];
                    int ny = j + dy[k];

                    if (nx>=0 && ny>=0 && nx<rows && ny<cols && grid[nx][ny]==-1)
                        cnt++;
                }
                grid[i][j] = cnt;
            }
        }
        List<List<Integer>> listGrid = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(grid[i][j]);
            }
            listGrid.add(row);
        }

        return listGrid;
    }
}