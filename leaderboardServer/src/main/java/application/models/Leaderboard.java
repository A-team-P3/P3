package application.models;

import java.util.List;

public class Leaderboard {
    public int id;
    public String name;
    public int size;
    public List<PlayerScore> playerScores;

    // Constructor
    public Leaderboard(int id, String name, int size, List<PlayerScore> playerScores) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.playerScores = playerScores;
    }

}
