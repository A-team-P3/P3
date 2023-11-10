package Backend.API;

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
        this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618);
        this.jedis = jedisPool.getResource();
        this.jedis.auth("MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9"); // Add your Redis password here
    }

    private Jedis getJedisConnection() {
        return jedis;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public Jedis getJedis() {
        return jedis;
    }

    private String randomName() {
        List<String> nouns = Arrays.asList("Gamer", "Eagle", "Cobra", "Priest", "Pilot", "Ace", "Officer", "Commander", "Dragon", "Swan", "Dolphin", "Hawk", "Vulture", "Mole", "Toucan", "Lizard", "Moose", "Bamboo", "Robber", "Painter", "Sheriff", "Judge", "Cook", "Baron", "King", "Lord", "Queen", "Emperor", "President", "Astronomer", "Astronaut", "Expert", "Slut");
        List<String> adjectives = Arrays.asList("Mystic", "Elite", "Disguising", "Mighty", "Big", "Tiny", "Aerobatic", "Lanky", "Fearful", "Shocking", "Striking", "Practical", "Unlucky", "Sweaty", "Floppy", "Pensive", "Steady", "Icky", "Unlawful", "Abnormal", "Friendly", "Receptive", "Maternal", "Juicy", "Grotesque", "Gimmicky", "Clumsy");
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