package application.models;

public class PlayerScore {
    public int score;
    public String name;
    public String region;
    public int rank;

    // Constructor
    public PlayerScore(int score, String name, String region, int rank) {
        this.score = score;
        this.name = name;
        this.region = region;
        this.rank = rank;
    }

}
