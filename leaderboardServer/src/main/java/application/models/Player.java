package application.models;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@RedisHash("Player")
public class Player implements Serializable {
    private String id;
    private String name;
    private String score;
    private String region;
    private String creationDate;
    private String rank;

    // Make new player constructor
    public Player(String id, String name, String score, String region) {
        if (id == null) {
            throw new IllegalArgumentException("Player ID cannot be null!");
        }
        
        this.id = id;
        this.name = name;
        this.score = score;
        this.region = region;
        this.creationDate = LocalDate.now().toString();
    }

    // Used to get the player with the rank
    public Player(String id, String name, String score, String region, String creationDate, String rank) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.region = region;
        this.creationDate = creationDate;
        this.rank = rank;
    }

    // Used when pulling a player from Redis
    public Player(String id, String name, String score, String region, String creationDate) {
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

    public String getCreationDate() {
        return creationDate;
    }

    public String getRank() {
        return rank;
    }
}