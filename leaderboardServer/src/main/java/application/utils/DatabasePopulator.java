package application.utils;

import application.services.DatabaseService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import application.utils.DatabaseConventions;
import redis.clients.jedis.Transaction;

import java.util.*;

public class DatabasePopulator {
    private Random rand = new Random();
    private final DatabaseConventions databaseConventions = new DatabaseConventions();
    private final DatabaseService databaseService = new DatabaseService();
    private Jedis jedis;

    public DatabasePopulator(Jedis jedis) {
        this.jedis = jedis;
    }

    public void populateDatabase(int leaderboardId, int numberOfPlayers) {
        // Select logical Redis database
        jedis = databaseService.selectDatabase(leaderboardId);

        Transaction transaction = jedis.multi();

        for (int i = 0; i < numberOfPlayers; i++) {
            int score = randomScore(numberOfPlayers * 100);
            String timestamp = String.valueOf(randomTimestamp());
            String id = userIdGenerator();

            transaction.zadd(databaseConventions.leaderboardSortedKeyString(leaderboardId), score, databaseConventions.leaderboardScoreKeyString(score, timestamp, id));
            transaction.hset(databaseConventions.leaderboardHashMapKeyString(leaderboardId), id, databaseConventions.leaderboardScoreKeyString(score, timestamp, id));

            // Add player as individual HashMap
            Map<String, String> fields = new HashMap<>();
                fields.put("id", id);
                fields.put("name", randomName());
                fields.put("score", String.valueOf(score));
                fields.put("region", randomRegion());
                fields.put("creationDate", timestamp);
                transaction.hmset(databaseConventions.playerObjectKeyString(id), fields);
        }
        transaction.exec();
    }

    private String userIdGenerator() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        final int USER_ID_LENGTH = 7;
        final Random random = new Random();
        StringBuilder userId = new StringBuilder(USER_ID_LENGTH);

        for (int i = 0; i < USER_ID_LENGTH; i++) {
            userId.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return userId.toString();
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

    private String randomCountry() {
        List<String> countries = Arrays.asList("DK", "SE", "GE", "UK", "US", "RU", "NO", "JP", "CH");
        return countries.get(rand.nextInt(countries.size()));
    }

    private String randomRegion() {
        List<String> regions = Arrays.asList("EU", "NA", "AS", "SA");
        return regions.get(rand.nextInt(regions.size()));
    }

    // Random timestamp between 01/01-2020 and 01/01-2024
    private long randomTimestamp() {
        long min = 1577836800L;             // 01/01-2020
        long max = 1735689600L;             // 01/01-2024
        long timeDifference = max - min;    // 1735689600 - 1577836800 = 158_852_800
        return min + (long) (Math.random() * timeDifference);   // e.x. 1577836800 + (0.5 * 158_852_800) = 164_676_240 = 01/07-2022
    }
}