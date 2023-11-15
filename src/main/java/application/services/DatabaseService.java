package application.services;

import application.utils.DatabasePopulator;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service //Service for interacting with the database to get user data
public class DatabaseService {

    private JedisPool jedisPool;
    private Jedis jedis;

    public DatabaseService() {
        //this.jedisPool = new JedisPool("130.225.39.42", 6379, "default", "tJ1Y37fGm5c2A2m6jCE0");
        new DatabasePopulator();
    }

    // TODO: check how we can connect in databasePopulator without 2 getJecisConnection methods
    public Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    private String leaderboardKeyString (int leaderboardId) {
        String key = "leaderboard:" + leaderboardId;
        return key;
    }

    // Returns a list of the members with the highest scores
    public List<Tuple> getMembersByRange(int leaderboardId, int start, int stop) {
        String leaderboardKey = leaderboardKeyString(leaderboardId);
        try (Jedis jedis = getJedisConnection()) {
            if (jedis.exists(leaderboardKey)) {
                if (start < stop) {
                    return jedis.zrevrangeWithScores(leaderboardKey, start, stop);
                } else {
                    return jedis.zrangeWithScores(leaderboardKey, stop, start);
                }
            }
        }
        catch(JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting scores");

        }
        return null;
    }

    public Integer getSize() {
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.zcard("leaderboard:1"));
        }
        catch(JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting size");
        }
        return null;
    }
}