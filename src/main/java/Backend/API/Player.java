package Backend.API;

public class Player {
    private int id;
    private String name;
    private String score;
    private String country;
    private String region;

    public Player(int id, String name, String score, String country, String region) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.country = country;
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    } // was INT

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }
}