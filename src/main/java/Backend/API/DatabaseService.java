package Backend.API;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service //Service for interacting with the database to get user data
public class DatabaseService {

    private List<Player> players = new ArrayList<>();
    private Random rand = new Random();
    private JedisPool jedisPool;
    private Jedis jedis;

    public DatabaseService() {
        // Establishing connection to database
        this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618);
        this.jedis = getJedisConnection();
        populateDatabase();
    }
    private Jedis getJedisConnection() {
        jedis = jedisPool.getResource();
        jedis.auth("MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9"); // Add your Redis password here
        return jedis;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public Jedis getJedis() {
        return jedis;
    }

    private String randomName() {
        List<String> nouns = Arrays.asList("Gamer", "Eagle", "Cobra", "Priest", "Pilot", "Ace", "Officer", "Commander", "Dragon", "Swan", "Dolphin", "Hawk", "Vulture", "Mole", "Toucan", "Lizard", "Moose", "Bamboo", "Robber", "Painter", "Sheriff", "Judge", "Cook", "Baron", "King", "Lord", "Queen", "Emperor", "President", "Astronomer", "Astronaut", "Expert");
        List<String> adjectives = Arrays.asList("Mystic", "Elite", "Disguising", "Mighty", "Big", "Tiny", "Aerobatic", "Lanky", "Fearful", "Shocking", "Striking", "Practical", "Unlucky", "Sweaty", "Floppy", "Pensive", "Steady", "Icky", "Unlawful", "Abnormal", "Friendly", "Receptive", "Maternal", "Juicy", "Grotesque", "Gimmicky");
        int noun = rand.nextInt(nouns.size());
        int adjective = rand.nextInt(adjectives.size());
        int number = rand.nextInt(100);

        return  adjectives.get(adjective) + nouns.get(noun) + number;
    }

    private String randomScore(int bound) {
        return String.valueOf(rand.nextInt(bound));

    }

    private String randomRegion() {
        List<String> regions = Arrays.asList("EU", "NA", "AS", "SA");
        return regions.get(rand.nextInt(regions.size()));
    }

    private String randomCountry() {
        List<String> countries = Arrays.asList("DK", "SE", "GE", "UK", "US", "RU", "NO", "JP", "CH");
        return countries.get(rand.nextInt(countries.size()));
    }

    public DatabaseService() {
        for (int i = 0; i<100; i++) {
            /*User temp =*/ new Player(i, randomName(), randomScore(1000), randomCountry(), randomRegion());
            //jedis.zadd("playersByPoints", Double.parseDouble(temp.getName()), temp.getScore()); //addning all the users to the playersByPoint stack
            // hash the rest of the users functions under the same id or something
        }
        //need to sort users by score primary with timestamp as tiebreaker
        //need a custom comparator
    }

    public int getSize() {
        return players.size();
    }

    // Returns a list of the players with the highest scores
    public List<String> getPlayersByPoints(){
        return jedis.zrevrange("playersByPoints", 0,5);
    }

    public void populateDatabase() {
        for (int i = 0; i < 100; i++) {
            jedis.zadd("playersByPoints", randomScore(10000), String.format(randomName()));
        }
    }

    public Player getUser(int index) {
        return players.get(index);
    }
}