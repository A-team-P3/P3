package application.services;

import application.models.Player;
import application.utils.DatabasePopulator;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.Tuple;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service //Service for interacting with the database to get user data
public class DatabaseService {

    private JedisPool jedisPool;

    public DatabaseService() {
        this.jedisPool = new JedisPool("130.225.39.42", 6379, "default", "tJ1Y37fGm5c2A2m6jCE0");
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
    /*
        public Player(int id, String name, String score, String country, String region) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.region = region;
    }
     */
    public void setPlayer(Player playerObject) {
        try (Jedis jedis = getJedisConnection()) {
            String key = "players:" + playerObject.getId();
            Map<String, String> hash = new HashMap<>();
            hash.put("id", playerObject.getId());
            hash.put("name", playerObject.getName());
            hash.put("region", playerObject.getRegion());
            hash.put("creationDate", playerObject.getCreationDate().toString());
            jedis.hset(key, hash);
        }
    }
    public Player getPlayer(String id) {
        try (Jedis jedis = getJedisConnection()) {
            Map<String,String> playerString = jedis.hgetAll("players:" + id);
            return new Player(
                    playerString.get("id"),
                    playerString.get("name"),
                    playerString.get("region"),
                    LocalDate.parse(playerString.get("creationDate"))
            );
        }
    }

}