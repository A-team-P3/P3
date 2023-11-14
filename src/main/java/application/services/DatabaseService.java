package application.services;

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

    private Random rand = new Random();
    private JedisPool jedisPool;
    private Jedis jedis;

    public DatabaseService() {
        this.jedisPool = new JedisPool("130.225.39.42", 6379, "default", "tJ1Y37fGm5c2A2m6jCE0");
        //populateDatabase();
    }
    private Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();// Add your Redis password here
        return jedis;
    }
    private String randomName() {
        List<String> nouns = Arrays.asList("Gamer", "Love", "Life", "Priest", "Pilot", "Business", "Officer", "Eater", "trafficker", "Dragon", "Swan", "Season", "Hawk", "Peasant", "Lizard", "Time", "Bamboo", "Licker", "Robber", "Painter", "Bone", "Juice", "Party", "Preacher", "Picker", "King", "Lord", "Queen", "Emperor", "President", "Astronomer", "Astronaut", "Expert", "Slut", "Hunter");
        List<String> adjectives = Arrays.asList("Mystic", "Elite", "Distinguished", "Mighty", "Big", "Tiny", "Filthy", "Lanky", "Fearful", "Slow", "Striking", "Slime", "Speedy", "Unlucky", "Sweaty", "Floppy", "Sad", "Steady", "Child", "Rat", "Lone", "Icky", "Unlawful", "Abnormal", "Friendly", "Receptive", "Maternal", "Juicy", "Grotesque", "Gimmicky", "Clumsy", "Satanic", "Unwashed", "Conservative");
        int noun = rand.nextInt(nouns.size());
        int adjective = rand.nextInt(adjectives.size());
        int number = rand.nextInt(100);

        return  adjectives.get(adjective) + nouns.get(noun) + number;
    }

    private int randomScore(int bound) {
        return rand.nextInt(bound);

    }
    private String leaderboardKeyString (int leaderboardId) {
        String key = "leaderboard:" + leaderboardId;
        return key;
    }

    public List<Tuple> getMembersByRange(int leaderboardId, int start, int stop) {
        String leaderboardKey = leaderboardKeyString(leaderboardId);
        try (Jedis jedis = getJedisConnection()) {
            if (jedis.exists(leaderboardKey)) {
                return jedis.zrevrangeWithScores(leaderboardKey, start, stop);
            }
        }
        catch(JedisException e) {
            // TODO: implement correct exception handling
            System.out.println("Error getting scores");
            return null;
        }
    }


    private String randomRegion() {
        List<String> regions = Arrays.asList("EU", "NA", "AS", "SA");
        return regions.get(rand.nextInt(regions.size()));
    }

    private String randomCountry() {
        List<String> countries = Arrays.asList("DK", "SE", "GE", "UK", "US", "RU", "NO", "JP", "CH");
        return countries.get(rand.nextInt(countries.size()));
    }

    public Integer getSize() {
        try (Jedis jedis = getJedisConnection()) {
            return Math.toIntExact(jedis.zcard("leaderboard1"));
        }
    }

    // Returns a list of the players with the highest scores
    public List<Tuple> getPlayersByPoints(int min, int max){

        try (Jedis jedis = getJedisConnection()) {
            if (max > min) {
                return jedis.zrevrangeWithScores("leaderboard1", min, max);
            }
            else {
                return jedis.zrangeWithScores("leaderboard1", max, min);
            }
        }
    }

    public void populateDatabase() {
        for (int i = 0; i < 100; i++) {
            jedis.zadd("leaderboard1", randomScore(10000), String.format(randomName()));
        }
    }

}