package application.models;

import java.time.LocalDate;
import java.util.Date;

public class Player {
    private String id;
    private String name;
    private String region;
    private LocalDate creationDate;

    //Make new player constructor
    public Player(String id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.creationDate = LocalDate.now();
    }
    //Update player constructor; Used when pulling a player from Redis
    public Player(String id, String name, String region, LocalDate creationDate) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.creationDate = creationDate;
    }




    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getRegion() {
        return region;
    }
    public LocalDate getCreationDate() {
        return creationDate;
    }
}