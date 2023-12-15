package application.services;

import application.models.Leaderboard;
import application.models.Player;
import application.models.PlayerScore;
import application.utils.*;

import org.springframework.stereotype.Service;

import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.resps.Tuple;

import java.util.*;


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

    public enum ServerType {
        AAU, CLOUD, LOCAL
    }

    //change server here
    private final ServerType serverType = ServerType.CLOUD;

    public DatabaseService() {
        switch (serverType) {
            case AAU -> this.jedisPool = new JedisPool(AAU_SERVER_IP, AAU_PORT, "default", AAU_SERVER_PASSWORD);
            case CLOUD -> this.jedisPool = new JedisPool(CLOUD_SERVER_IP, CLOUD_PORT, "default", CLOUD_SERVER_PASSWORD);
            case LOCAL -> this.jedisPool = new JedisPool("localhost", 6379);
            default -> throw new IllegalArgumentException("Invalid server type");
        }
    }

    public Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

    // For testing purposes
    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
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
    public List<Player> getMembersByRange(int leaderboardId, int start, int stop) {
        String leaderboardKey = leaderboardSortedKeyString(leaderboardId);
        try (Jedis jedis = getJedisConnection()) {
            if (jedis.exists(leaderboardKey)) {
                if (start < stop) {
                    List<Tuple> playerTuple = jedis.zrevrangeWithScores(leaderboardKey, start, stop);
                    return tupleToPlayerObject(playerTuple, jedis, start);
                } else {
                    List<Tuple> playerTuple = jedis.zrangeWithScores(leaderboardKey, stop, start);
                    return tupleToPlayerObject(playerTuple, jedis, start);
                }
            }
        } catch (JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting scores");
        }
        return null;
    }

    private List<Player> tupleToPlayerObject(List<Tuple> tuples, Jedis jedis, int start) {
        List<Player> players = new ArrayList<>();

        Pipeline pipeline = jedis.pipelined();

        for (Tuple tuple : tuples) {
            String[] parts = tuple.getElement().split(":");
            String userId = parts[2];

            // Add hmget commands to the pipeline
            pipeline.hmget("player:" + userId, "name", "region");
        }

        // Execute pipeline commands and retrieve responses
        List<Object> responses = pipeline.syncAndReturnAll();

        int counter = start;

        // Process responses and create Player objects
        for (int i = 0; i < tuples.size(); i++) {
            counter++;
            Tuple tuple = tuples.get(i);
            String[] parts = tuple.getElement().split(":");
            String userScore = parts[0];
            String userCreationDate = parts[1];
            String userId = parts[2];

            List<String> responseValues = (List<String>) responses.get(i); //TODO Exception her
            String name = responseValues.get(0);
            String region = responseValues.get(1);

            players.add(new Player(userId, name, userScore, region, userCreationDate, String.valueOf(counter)));
        }

        return players;
    }

    public Integer getSize(int leaderboardId) {
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.zcard(leaderboardSortedKeyString(leaderboardId)));
        }
        catch (JedisException e) {
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
            hash.put("score", playerObject.getScore());
            hash.put("region", playerObject.getRegion());
            hash.put("creationDate", playerObject.getCreationDate());
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
                    playerString.get("score"),
                    playerString.get("region"),
                    playerString.get("creationDate")
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

    // TODO: can be optimized in terms of time complexity
    // Returns a list of players with a matching name (case insensitive) from a specified leaderboard
    public List<Player> findPlayersByName(String specifiedName, int leaderboardId) {
        List<Player> matchingPlayers = new ArrayList<>();

        // If the specified name is less than 3 characters, return an empty list
        if (specifiedName.length() < 3)
            return matchingPlayers;

        try (Jedis jedis = getJedisConnection()) {
            // Get all entries (name and id) from the playerNames HashMap
            Map<String, String> nameAndIdMap = jedis.hgetAll("playerNames:" + leaderboardId);

            int counter = 0;

            // For each entry in the map, get the name and id
            for (Map.Entry<String, String> entry : nameAndIdMap.entrySet()) {
                String playerName = entry.getKey();
                String playerId = entry.getValue();

                // Check if this player name contains the specified name
                // TODO: make faster (sync?)
                if (playerName.toLowerCase().contains(specifiedName.toLowerCase())) {
                    counter++;
                    if (counter > 50) {
                        break;
                    }
                    // Fetch score and region with ID, and pipelining to improve performance
                    Pipeline pipeline = jedis.pipelined();
                    Response<String> scoreResponse = pipeline.hget("player:" + playerId, "score");
                    Response<String> regionResponse = pipeline.hget("player:" + playerId, "region");
                    Response<String> creationDateResponse = pipeline.hget("player:" + playerId, "creationDate");
                    pipeline.sync();    // Execute all commands in the pipeline

                    // Get the score, region and creation date from the responses
                    String score = scoreResponse.get();
                    String region = regionResponse.get();
                    String creationDate = creationDateResponse.get();
                    String playerValue = score + ":" + creationDate + ":" + playerId;

                    // Get the rank of the player
                    long rank = jedis.zrevrank("leaderboardSorted:" + leaderboardId, playerValue);
                    Player player = new Player(playerId, playerName, score, region, creationDate, String.valueOf(rank + 1));

                    matchingPlayers.add(player);
                }
            }

            // Sort the list of matching players by score
            Comparator<Player> byScore = Comparator.comparingInt(player -> Integer.parseInt(player.getScore()));
            matchingPlayers.sort(byScore.reversed());       // Sort in descending order
        }
        catch (JedisException e) {
            System.err.println(e + ": error in finding players by name!");
            return null;
        }

        return matchingPlayers;
    }
    public List<Integer> getLeaderboardAmount() {
        List<Integer> leaderboardAmount = new ArrayList<>();
        try (Jedis jedis = getJedisConnection()) {
            Set<String> sortedSets = jedis.keys("leaderboardSorted:*");
            for(String set : sortedSets) {
                String[] splitted =  set.split(":");

                leaderboardAmount.add(Integer.valueOf(splitted[1]));
            }
        } catch (JedisException e) {
            System.err.println(e + ": error in finding leaderboard amount");
        }
        Collections.sort(leaderboardAmount);
        return leaderboardAmount;
    }

    public void wipeLeaderboard(int leaderboardId) {
        try (Jedis jedis = getJedisConnection()) {
            // Delete all player hashes belonging to the leaderboards
            Set<String> playerIds = jedis.hkeys(leaderboardHashMapKeyString(leaderboardId));

            Pipeline pipeline = jedis.pipelined();
            for (String playerId : playerIds) {
                pipeline.del(playerObjectKeyString(playerId));
            }

            // Delete specified leaderboards and name-id mapping
            pipeline.del(leaderboardSortedKeyString(leaderboardId));
            pipeline.del(leaderboardHashMapKeyString(leaderboardId));
            pipeline.del("playerNames:" + leaderboardId);

            pipeline.sync();
        }
        catch (JedisException e) {
            System.err.println(e + ": error wiping specified leaderboard!");
        }
    }

    public void wipeDatabase() {
        try (Jedis jedis = getJedisConnection()) {
            jedis.flushAll();
        }
        catch (JedisException e) {
            System.err.println(e + ": error wiping entire database!");
        }
    }
}