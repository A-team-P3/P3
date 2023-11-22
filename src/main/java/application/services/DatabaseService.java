package application.services;

import application.utils.DatabasePopulator;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.Tuple;
import java.util.List;


@Service //Service for interacting with the database to get user data
public class DatabaseService {

    private JedisPool jedisPool;

    public DatabaseService() {
        //this.jedisPool = new JedisPool("130.225.39.42", 6379, "default", "tJ1Y37fGm5c2A2m6jCE0");
        //this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618, "default", "MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9");
        this.jedisPool = new JedisPool("localhost", 6379);
        new DatabasePopulator(jedisPool);
    }

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

    public Integer getSize(int leaderboardId) {
        String leaderboardKey = leaderboardKeyString(leaderboardId);
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.zcard(leaderboardKey));
        }
        catch(JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting size");
        }
        return null;
    }
}