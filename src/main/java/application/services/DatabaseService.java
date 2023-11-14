package application.services;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
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
        establishDatabaseConnection();
        //populateDatabase();
    }

    private void establishDatabaseConnection() {
        // Establishing connection to database
        this.jedisPool = new JedisPool("130.225.39.42", 6379);
        this.jedis = jedisPool.getResource();
        this.jedis.auth("tJ1Y37fGm5c2A2m6jCE0"); // Add your Redis password here
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

    private String randomRegion() {
        List<String> regions = Arrays.asList("EU", "NA", "AS", "SA");
        return regions.get(rand.nextInt(regions.size()));
    }

    private String randomCountry() {
        List<String> countries = Arrays.asList("DK", "SE", "GE", "UK", "US", "RU", "NO", "JP", "CH");
        return countries.get(rand.nextInt(countries.size()));
    }

    public Integer getSize() {
        return Math.toIntExact(jedis.zcard("playersByPoints"));

    }

    // Returns a list of the players with the highest scores
    public List<Tuple> getPlayersByPoints(int min, int max){
        if (max > min)
        {
            return jedis.zrevrangeWithScores("playersByPoints", min,max);
        }
        else {
            return jedis.zrangeWithScores("playersByPoints", max,min);
        }
    }

    public String getPointsByPlayers(String player) {
        return  jedis.zscore("playersByPoints", player).toString();
    }


    public void populateDatabase() {
        for (int i = 0; i < 100; i++) {
            jedis.zadd("playersByPoints", randomScore(10000), String.format(randomName()));
        }
    }

}