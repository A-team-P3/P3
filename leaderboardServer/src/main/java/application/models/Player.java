package application.models;

import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDate;

@RedisHash("Player")
public class Player implements Serializable {
    private String id;
    private String name;
    private String score;
    private String region;
    private LocalDate creationDate;
    private String rank;

    // Make new Player constructor
    public Player(String id, String name, String score, String region) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.region = region;
        this.creationDate = LocalDate.now();
    }

    public Player(String id, String name, String score, String region, String rank) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.region = region;
        this.creationDate = LocalDate.now();
        this.rank = rank;
    }

    // Update player constructor (used when pulling a player from Redis)
    public Player(String id, String name, String score, String region, LocalDate creationDate) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.region = region;
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getRegion() {
        return region;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}