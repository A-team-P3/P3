package Backend.API;

public class User {
    private int id;
    private String name;
    private int score;
    private String country;
    private String region;

    //add timestamp (unless redis handles it)

    public User(int id, String name, int score, String country, String region) {
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

    public int getScore() {
        return score;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }
}