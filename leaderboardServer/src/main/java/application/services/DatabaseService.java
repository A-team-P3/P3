package application.services;

import application.models.Leaderboard;
import application.models.Player;
import application.models.PlayerScore;
import application.utils.*;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.Tuple;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service //Service for interacting with the database to get user data
public class DatabaseService {
    private final DatabaseConventions databaseConventions = new DatabaseConventions();
    private JedisPool jedisPool;

    private final String AAU_SERVER_IP = "130.225.39.42";
    private final int AAU_PORT = 6379;
    private final String AAU_SERVER_PASSWORD = "tJ1Y37fGm5c2A2m6jCE0";

    private final String CLOUD_SERVER_IP = "redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com";
    private final int CLOUD_PORT = 12618;
    private final String CLOUD_SERVER_PASSWORD = "MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9";

    public DatabaseService() {
        //this.jedisPool = new JedisPool("130.225.39.42", 6379, "default", "tJ1Y37fGm5c2A2m6jCE0");
        this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618, "default", "MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9");
        //this.jedisPool = new JedisPool("localhost", 6379);
        //new DatabasePopulator(jedisPool);
    }

    public Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    private String leaderboardSortedKeyString(int leaderboardId) {
        return databaseConventions.leaderboardSortedKeyString(leaderboardId);
    }

    private String leaderboardHashMapKeyString(int leaderboardId) {
        return databaseConventions.leaderboardHashMapKeyString(leaderboardId);
    }

    private String leaderboardScoreKeyString(int score, String timestamp, String playerId) {
        return databaseConventions.leaderboardScoreKeyString(score, timestamp, playerId);
    }

    private String playerObjectKeyString(String playerId) {
        return databaseConventions.playerObjectKeyString(playerId);
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
        } catch (JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting scores");
        }
        return null;
    }

    public Integer getSize(int leaderboardId) {
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.zcard(leaderboardSortedKeyString(leaderboardId)));
        } catch (JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting size");
        }
        return null;
    }

    public String setScore(String playerId, int newScore, int leaderboardId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        try (Jedis jedis = getJedisConnection()) {
            if (isPlayerExisting(playerId, leaderboardId)) {
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
                } else
                    return "The player's existing score is higher!";
            } else {
                // Add non-existing player to the leaderboard
                jedis.hset(leaderboardHashMapKeyString(leaderboardId), playerId, leaderboardScoreKeyString(newScore, timestamp, playerId));
                jedis.zadd(leaderboardSortedKeyString(leaderboardId), newScore, leaderboardScoreKeyString(newScore, timestamp, playerId));
                return "Player does not exist and is therefore added to the leaderboard";
            }
        } catch (JedisException e) {
            System.out.println(e + ": error setting score!");
        }
        return null;
    }

    public boolean isPlayerExisting(String playerId, int leaderboardId) {
        try (Jedis jedis = getJedisConnection()) {
            if (jedis.hexists(leaderboardSortedKeyString(leaderboardId), playerId)) {
                return true;
            }
        } catch (JedisException e) {
            System.err.println(e + ": error checking if player exists!");
        }
        return false;
    }

    public void setPlayerObject(Player playerObject) {
        try (Jedis jedis = getJedisConnection()) {
            String key = "player:" + playerObject.getId();
            Map<String, String> hash = new HashMap<>();
            hash.put("id", playerObject.getId());
            hash.put("name", playerObject.getName());
            hash.put("region", playerObject.getRegion());
            hash.put("creationDate", playerObject.getCreationDate().toString());
            jedis.hset(key, hash);
        }
    }

    public Player getPlayerObject(String id) {
        try (Jedis jedis = getJedisConnection()) {
            Map<String, String> playerString = jedis.hgetAll(playerObjectKeyString(id));
            if (playerString.isEmpty()) {
                return null;
            } //TODO: Implement correct exception handling
            return new Player(
                    playerString.get("id"),
                    playerString.get("name"),
                    playerString.get("region"),
                    LocalDate.parse(playerString.get("creationDate"))
            );
        }
    }

    public void populateLeaderboard(int leaderboardId, int numberOfPlayers) {
        try (Jedis jedis = getJedisConnection()) {
            DatabasePopulator databasePopulator = new DatabasePopulator(jedis);
            databasePopulator.populateDatabase(leaderboardId, numberOfPlayers);
        }
        catch (Exception e) {
            System.err.println(e + ": error populating database!");
        }
    }
/* Old code, creates a request to redis for each player in the range
    public Leaderboard getScoreByRange(int leaderboardId, int start, int stop) {
        try (Jedis jedis = getJedisConnection()) {
            List<PlayerScore> scores = new ArrayList<>();
            List<String> result = jedis.zrevrange(leaderboardSortedKeyString(leaderboardId), start - 1, stop - 1);

            for (int i = 0; i < result.size(); i++) {
                String[] parts = result.get(i).split(":");
                int playerScore = Integer.parseInt(parts[0]);
                String userId = parts[2];

                // Fetch additional player data like region
                String region = jedis.hget("player:" + userId, "region");

                // Rank is the index + 1
                int rank = start + i;

                PlayerScore playerScoreObj = new PlayerScore(playerScore, userId, region, rank);
                scores.add(playerScoreObj);
            }
            return new Leaderboard(leaderboardId, "Leaderboard Name", scores.size(), scores);
        }
        // Handle exceptions if necessary
    } */
    public Leaderboard getScoresByRange(int leaderboardId, int start, int stop) {
        try (Jedis jedis = getJedisConnection()) {
            List<PlayerScore> scores = new ArrayList<>();
            List<String> result = jedis.zrevrange(leaderboardSortedKeyString(leaderboardId), start - 1, stop - 1);

            Pipeline pipeline = jedis.pipelined();
            Map<String, Response<String>> regionResponses = new HashMap<>();

            for (String member : result) {
                String[] parts = member.split(":");
                String userId = parts[2];
                regionResponses.put(userId, pipeline.hget("player:" + userId, "region"));
            }

            pipeline.sync();

            int rankCounter = start;
            for (String member : result) {
                String[] parts = member.split(":");
                int playerScore = Integer.parseInt(parts[0]);
                String userId = parts[2];
                String region = regionResponses.get(userId).get();

                PlayerScore playerScoreObj = new PlayerScore(playerScore, userId, region, rankCounter);
                scores.add(playerScoreObj);
                rankCounter++;
            }

            return new Leaderboard(leaderboardId, "Leaderboard Name", scores.size(), scores);
        }
        // Handle exceptions if necessary
    }

    // TODO: include score and rank
    // Returns a list of players with a matching name (case insensitive) from a specified leaderboard
    public List<Player> findPlayersByName(String name, int leaderboardId) {

        try (Jedis jedis = getJedisConnection()) {
            List<Player> matchingPlayers = new ArrayList<>();

            // List of all players in the specified leaderboardHashMap
            List<String> leaderboard = jedis.hvals(leaderboardHashMapKeyString(leaderboardId));

            // For each player in this leaderboard, check if the name contains the specified name
            for (String playerString : leaderboard) {
                String[] parts = playerString.split(":");

                // Get id
                String userId = parts[2];

                // Use id to find and get name from player's individual HashMap
                String playerHashName = jedis.hget("player:" + userId, "name");

                // Add player to list if his name contains the specified name
                if (playerHashName.toLowerCase().contains(name.toLowerCase())) {
                    String region = jedis.hget("player:" + userId, "region");
                    matchingPlayers.add(new Player(userId, playerHashName, region));
                }
            }
            return matchingPlayers;
        }
    }
}