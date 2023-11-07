package Backend.API;



import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service //Service for interacting with the database to get user data
public class UserService {
    public static Jedis getJedis() {
        return jedis;
    }

    //establishing connection to database
    static Jedis jedis = new Jedis("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618);
    private List<User> users = new ArrayList<>();

    private Random rand = new Random();

    private String RandomName() {
        List<String> nouns = Arrays.asList("Gamer", "Eagle", "Cobra", "Priest", "Pilot", "Ace", "Officer", "Commander", "Dragon", "Swan", "Dolphin", "Hawk", "Vulture", "Mole", "Toucan", "Lizard", "Moose", "Bamboo", "Robber", "Painter", "Sheriff", "Judge", "Cook", "Baron", "King", "Lord", "Queen", "Emperor", "President", "Astronomer", "Astronaut", "Expert");
        List<String> adjectives = Arrays.asList("Mystic", "Elite", "Disguising", "Mighty", "Big", "Tiny", "Aerobatic", "Lanky", "Fearful", "Shocking", "Striking", "Practical", "Unlucky", "Sweaty", "Floppy", "Pensive", "Steady", "Icky", "Unlawful", "Abnormal", "Friendly", "Receptive", "Maternal", "Juicy", "Grotesque", "Gimmicky");
        int noun = rand.nextInt(nouns.size());
        int adjective = rand.nextInt(adjectives.size());
        int number = rand.nextInt(100);

        return adjectives.get(adjective) + nouns.get(noun) + number;
    }

    private int RandomScore(int bound) {
        return rand.nextInt(bound);
    }

    private String RandomRegion() {
        List<String> regions = Arrays.asList("EU", "NA", "AS", "SA");
        return regions.get(rand.nextInt(regions.size()));
    }

    private String RandomCountry() {
        List<String> countries = Arrays.asList("DK", "SE", "GE", "UK", "US", "RU", "NO", "JP", "CH");
        return countries.get(rand.nextInt(countries.size()));
    }

    public UserService() {
        for (int i = 0; i<100; i++) {
            users.add(new User(i, RandomName(), RandomScore(1000), RandomCountry(), RandomRegion()));
        }
        //need to sort users by score primary with timestamp as tiebreaker
        //need a custom comparator
    }

    public int GetSize() {
        return users.size();
    }

    //function that returns a list of the players with the highest scores
    public List<String> getPlayersByPoints(){
        return jedis.zrevrange("playersByPoints", 0,5);
    }

    public User GetUser(int index) {
        return users.get(index);
    }

}
