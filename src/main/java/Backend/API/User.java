package Backend.API;

public class User {
    private int id;
    private String name;
    private String score; //was INT
    private String country;
    private String region;

    //add timestamp (unless redis handles it)

    public User(int id, String name, String score /*was int*/, String country, String region) {
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