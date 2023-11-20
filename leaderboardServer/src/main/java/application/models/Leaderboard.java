package application.models;

import java.util.List;

public class Leaderboard {
    private int id;
    private String name;
    private int size;
    private List<PlayerScore> playerScores;

    // Constructor, getters, and setters
    public Leaderboard(int id, String name, int size, List<PlayerScore> playerScores) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.playerScores = playerScores;
    }

    // Getters and Setters
    // ...
}
