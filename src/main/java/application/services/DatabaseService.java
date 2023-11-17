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
        this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618, "default", "MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9");
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

    public String setScore(String playerId, int newScore, int leaderboardId) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        try (Jedis jedis = getJedisConnection()) {
            if(isPlayerExisting(playerId, leaderboardId)) {
                // Get value of playerId from hashSet leaderboard
                String initialHashValue = jedis.hget("hashLeaderboard:" + leaderboardId, playerId);

                // Use hashValue value to get the existing score of the playerId
                List<Double> existingScore = jedis.zmscore("sortedLeaderboard:" + leaderboardId, initialHashValue);
                if (existingScore.get(0) < newScore) {
                    // Update leaderboard hash with score

                    jedis.hset("hashLeaderboard:" + leaderboardId, playerId, newScore + ":" + timestamp + ":" + playerId);

                    // Remove existing score in sorted set with initial hashValue
                    jedis.zrem("sortedLeaderboard:" + leaderboardId, initialHashValue);

                    // Get the new hashValue
                    String newHashValue = jedis.hget("hashLeaderboard:" + leaderboardId, playerId);

                    // Update sortedSet leaderboard with new HashValue
                    jedis.zadd("sortedLeaderboard:" + leaderboardId, newScore, newHashValue);
                    return "Score changed";
                }
                else
                    return "The player's existing score is higher!";
            }
            else {
                // Add non-existing player to the leaderboard
                jedis.hset("leaderboard:" + leaderboardId, playerId, newScore + ":" + timestamp + ":" + playerId);
                jedis.zadd("sortedLeaderboard:" + leaderboardId, newScore, newScore + ":" + timestamp + ":" + playerId);
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
            if (jedis.hexists("leaderboard:" + leaderboardId, playerId)) {
                return true;
            }
        }
        catch (JedisException e) {
            System.err.println(e + ": error checking if player exists!");
        }
        return false;
    }

}