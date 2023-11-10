package Backend.API;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
public class DBService {

    private final JedisPool jedisPool;

    public DBService() {
        // Assuming you want to configure the pool in the constructor,
        // otherwise you could move this to an @PostConstruct method or a configuration class.
        this.jedisPool = new JedisPool("130.225.39.42", 6379); // Add your host and port here
    }

    private Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();
        jedis.auth("tJ1Y37fGm5c2A2m6jCE0"); // Add your Redis password here
        return jedis;
    }

    private class UserIdGenerator {
        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        private static final int USER_ID_LENGTH = 7;
        private final Random random = new Random();
        public String createNewUserID() {
            StringBuilder userId = new StringBuilder(USER_ID_LENGTH);
            for (int i = 0; i < USER_ID_LENGTH; i++) {
                userId.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            return userId.toString();
        }
    }
    public ResponseEntity<String> getScoresByRange(int leaderboardId, int start, int stop) {
        String leaderboardKey = "leaderboard" + leaderboardId;
        try (Jedis jedis = getJedisConnection()) {
            if (!jedis.exists(leaderboardKey)) {
                return new ResponseEntity<>("Leaderboard does not exist", HttpStatus.NOT_FOUND);
            } else {
                String scores = jedis.zrevrangeWithScores(leaderboardKey, start, stop).toString();
                return new ResponseEntity<>(scores, HttpStatus.OK);
            }
        } catch (JedisException e) {
            // Log the exception as needed
            return new ResponseEntity<>("Error getting scores", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Function to set a user's score in a leaderboard. Can update existing or add new user and score.
    //Checks if the leaderboard exists, and if the user already exists in the leaderboard.
    //If the user already exists, it checks if the new score is higher than the old score.
    //If the new score is higher, it updates the user's score in the leaderboard.
    //If the new score is lower, it does nothing.
    private ResponseEntity<String> setScore(int leaderboardId, int newScore, String userId) {
        //Define leaderboard name, convention "leaderboard" + leaderboardId
        String leaderboardKey = "leaderboard" + leaderboardId;
        //Define leaderboard hash table name, convention "leaderboard" + leaderboardId + "hash"
        //E.g. leaderboard3hash
        String leaderboardHashKey = leaderboardKey + "hash";
        //Create timestamp
        long timestamp = System.currentTimeMillis();
        //Format new score entry for database
        String newScoreEntry = newScore + ":" + timestamp + ":" + userId;
        //Connect to database
        try (Jedis jedis = getJedisConnection()) {
            //Check if leaderboard exists
            if (!jedis.exists(leaderboardKey)) {
                return new ResponseEntity<>("Leaderboard does not exist", HttpStatus.NOT_FOUND);
            }
            //****I think the following logic could be packed into a user-search function*****
            //Get user's old score from leaderboard hash table
            String oldScoreEntry = jedis.hget(leaderboardHashKey, userId);
            //Check if user exists in leaderboard hash table
            if (oldScoreEntry != null) {
                //Split the old newScore entry into its components (oldScore, timestamp, userId)
                String[] userScoreArray = oldScoreEntry.split(":");
                int oldScore = Integer.parseInt(userScoreArray[0]);
                long Timestamp = Long.parseLong(userScoreArray[1]);
                if (oldScore < newScore) { //If the new score is higher than the old score:
                    jedis.hset(leaderboardHashKey, userId, newScoreEntry);
                    jedis.zrem(leaderboardKey, oldScoreEntry); //Remove the old score from the sorted set
                    jedis.zadd(leaderboardKey, newScore, newScoreEntry); //Add new score to the sorted set
                    return new ResponseEntity<>("Old score (" + oldScore + ")removed, new score (" + newScore + ") added to leaderboard", HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("New score not higher than old score, not added", HttpStatus.OK);
                } //If the new score is lower than the old score, do nothing
            } else { //If the score didn't already exist in the leaderboard:
                jedis.hset(leaderboardHashKey, userId, newScoreEntry); //Add to leaderboard hash table
                jedis.zadd(leaderboardKey, newScore, newScoreEntry); //Add to leaderboard
                return new ResponseEntity<>("New score (" + newScore + ") added to leaderboard", HttpStatus.CREATED);
            }
        } catch (JedisException e) {
            // Log the exception as needed
            return new ResponseEntity<>("Error adding score to leaderboard", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<String> findByUserId(int leaderboardId, String userId) {
        String leaderboardKey = "leaderboard" + leaderboardId;
        String leaderboardHashKey = leaderboardKey + "hash";

        try (Jedis jedis = getJedisConnection()) {
            // Check if the leaderboard exists
            if (!jedis.exists(leaderboardKey)) {
                return new ResponseEntity<>("Leaderboard does not exist", HttpStatus.NOT_FOUND);
            }

            // Get the user's score entry from the hash table
            String userScoreEntry = jedis.hget(leaderboardHashKey, userId);
            if (userScoreEntry == null) {
                return new ResponseEntity<>("User not found in leaderboard", HttpStatus.NOT_FOUND);
            }

            // Parse the score from the entry
            String[] entryComponents = userScoreEntry.split(":");
            int score = Integer.parseInt(entryComponents[0]);

            // Find the rank of the user's score in the sorted set
            Long rank = jedis.zrevrank(leaderboardKey, userScoreEntry);
            if (rank == null) {
                return new ResponseEntity<>("User score not found in leaderboard", HttpStatus.NOT_FOUND);
            }

            // Return the rank and score
            String result = String.format("User ID: %s, Rank: %d, Score: %d", userId, rank + 1, score); // rank + 1 because rank is 0-indexed
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (JedisException e) {
            // Log the exception as needed
            return new ResponseEntity<>("Error finding user in leaderboard", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<String> getLeaderboard(int leaderboardId) throws Exception {
        String leaderboardKey = "leaderboard" + leaderboardId;
        try (Jedis jedis = getJedisConnection()) {
            if (!jedis.exists(leaderboardKey)) {
                return new ResponseEntity<>("Leaderboard does not exist", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(jedis.zrevrange(leaderboardKey, 0, -1).toString(), HttpStatus.OK);
            }
        }
    } //how do I use this method? I tried http://localhost:8080/getLeaderboard?leaderboardId=1
    public ResponseEntity<String> createLeaderboard(int leaderboardId) throws Exception {
        String leaderboardKey = "leaderboard" + leaderboardId;
        try (Jedis jedis = getJedisConnection()) {
            if (jedis.exists(leaderboardKey)) {
                return new ResponseEntity<>("Leaderboard already exists", HttpStatus.CONFLICT);
            } else {
                jedis.zadd(leaderboardKey, 0, "initial:0"); // Adding an initial value to create the set
               // jedis.del(leaderboardKey); // Removing the initial value so the set is empty
                return new ResponseEntity<>("Leaderboard created", HttpStatus.OK);
            }
        }
    } //how do I use this method? I tried http://localhost:8080/createLeaderboard?leaderboardId=1
    public ResponseEntity<String> addScoreToLeaderboard(int leaderboardId, int score, String userId) {
        return setScore(leaderboardId, score, userId);
    }//how do i use this method? I tried http://localhost:8080/addScoreToLeaderboard?leaderboardId=1&score=1&userId=1

    //Function to populate database with random user ID's and scores between 1 and 10000
    public ResponseEntity<String> populateDatabase(int leaderboardId, int numberOfUsers) throws Exception {
        String leaderboardKey = "leaderboard" + leaderboardId;
        UserIdGenerator userIdGenerator = new UserIdGenerator();
        try (Jedis jedis = getJedisConnection()) {
            if (!jedis.exists(leaderboardKey)) {
                return new ResponseEntity<>("Leaderboard does not exist", HttpStatus.NOT_FOUND);
            } else {
                for (int i = 0; i < numberOfUsers; i++) {
                    int score = (int) (Math.random() * 10000);
                    String userId = userIdGenerator.createNewUserID();
                    setScore(leaderboardId, score, userId);
                }
                return new ResponseEntity<>("Leaderboard populated", HttpStatus.OK);
            }
        }
    } //how do I use this method? I tried http://localhost:8080/populateDatabase?leaderboardId=1&numberOfUsers=1000
}