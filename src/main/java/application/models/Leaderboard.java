package application.models;

import java.util.List;

public class Leaderboard {
    public int id;
    public String name;
    public List<PlayerScore> playerScores;

    // Constructor, getters, and setters
    public Leaderboard(int id, String name, List<PlayerScore> playerScores) {
        this.id = id;
        this.name = name;
        this.playerScores = playerScores;
    }


    // Getters and Setters
    // ...
}
