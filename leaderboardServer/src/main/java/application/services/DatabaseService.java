package application.services;

import application.models.Player;
import application.utils.DatabasePopulator;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
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
        //this.jedisPool = new JedisPool("130.225.39.42", 6379, "default", "tJ1Y37fGm5c2A2m6jCE0");
        this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618, "default", "MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9");
        new DatabasePopulator(jedisPool);
    }

    public Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    private String leaderboardSortedKeyString(int leaderboardId) {
        return "leaderboardSorted:" + leaderboardId;
    }
    private String leaderboardHashMapKeyString (int leaderboardId) {
        return "leaderboardHashMap:" + leaderboardId;
    }
    private String leaderboardScoreKeyString (int score, String timestamp, String playerId) {
        return score + ":" + timestamp + ":" + playerId;
    }

    // Returns a list of the members with the highest scores
    public List<Tuple> getMembersByRange(int leaderboardId, int start, int stop) {
        String leaderboardKey = leaderboardSortedKeyString(leaderboardId);
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
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.zcard(leaderboardSortedKeyString(leaderboardId)));
        }
        catch(JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting size");
        }
        return null;
    }

    public String setScore(String playerId, int newScore, int leaderboardId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        try (Jedis jedis = getJedisConnection()) {
            if(isPlayerExisting(playerId, leaderboardId)) {
                // Get value of playerId from hashSet leaderboard
                String initialHashValue = jedis.hget(leaderboardHashMapKeyString(leaderboardId), playerId);

                // Use hashValue value to get the existing score of the playerId
                List<Double> existingScore = jedis.zmscore(leaderboardSortedKeyString(leaderboardId), initialHashValue);
                if (existingScore.get(0) < newScore) {
                    // Update leaderboard hash with score

                    jedis.hset(leaderboardHashMapKeyString(leaderboardId), playerId, leaderboardScoreKeyString(newScore, timestamp, playerId));

                    // Remove existing score in sorted set with initial hashValue
                    jedis.zrem(leaderboardSortedKeyString(leaderboardId), initialHashValue);

                    // Get the new hashValue
                    String newHashValue = jedis.hget(leaderboardHashMapKeyString(leaderboardId), playerId);

                    // Update sortedSet leaderboard with new HashValue
                    jedis.zadd(leaderboardSortedKeyString(leaderboardId), newScore, newHashValue);
                    return "Score changed";
                }
                else
                    return "The player's existing score is higher!";
            }
            else {
                // Add non-existing player to the leaderboard
                jedis.hset(leaderboardHashMapKeyString(leaderboardId), playerId, leaderboardScoreKeyString(newScore, timestamp, playerId));
                jedis.zadd(leaderboardSortedKeyString(leaderboardId), newScore, leaderboardScoreKeyString(newScore, timestamp, playerId));
                return "Player does not exist and is therefore added to the leaderboard";
            }
        }
        catch (JedisException e) {
            System.out.println(e + ": error setting score!");
        }
        return null;
    }

    public boolean isPlayerExisting(String playerId, int leaderboardId) {
        try (Jedis jedis = getJedisConnection()) {
            if (jedis.hexists(leaderboardSortedKeyString(leaderboardId), playerId)) {
                return true;
            }
        }
        catch (JedisException e) {
            System.err.println(e + ": error checking if player exists!");
        }
        return false;
    }
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