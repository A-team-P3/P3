package application.services;

import application.models.Player;
import application.utils.DatabaseConventions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DatabaseServiceTest {
    DatabaseService databaseService;
    JedisPool jedisPoolMock;
    Jedis jedisMock;
    //Player testPlayer;
    //DatabaseConventions databaseConventions;

    @BeforeEach
    void setUp() {
        // Create mock objects using Mockito to control their behavior and isolate tested methods
        jedisPoolMock = Mockito.mock(JedisPool.class);
        jedisMock = Mockito.mock(Jedis.class);
        when(jedisPoolMock.getResource()).thenReturn(jedisMock);

        databaseService = new DatabaseService();
        databaseService.jedisPool = jedisPoolMock;

        //testPlayer = new Player("ABCD123", "Bruce Wayne", "1337", "NA", LocalDate.now().toString());
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(jedisPoolMock, jedisMock);
        jedisPoolMock = null;
        jedisMock = null;
        databaseService = null;

        //testPlayer = null;
    }

    @Test
    void databaseServiceShouldExist() {
        assertNotNull(databaseService);
    }

    @Test
    void jedisConnectionShouldBeAlive() {
        Jedis result = databaseService.getJedisConnection();
        assertNotNull(result);

        // Verify that the mock objects were used as expected
        Mockito.verify(jedisPoolMock).getResource();
    }

    @Test
    void sizeShouldBe42() {
        when(jedisMock.zcard("leaderboardSorted:1")).thenReturn(42L);
        Integer result = databaseService.getSize(1);
        assertEquals(42, result);
    }

    @Test
    void playerShouldExist() {
        when(jedisMock.hexists("leaderboardSorted:1", "ABCD123")).thenReturn(true);
        boolean result = databaseService.isPlayerExisting("ABCD123", 1);
        assertTrue(result);
    }

    @Test
    void playerShouldNotExist() {
        when(jedisMock.hexists("leaderboardSorted:1", "1234ABC")).thenReturn(false);
        boolean result = databaseService.isPlayerExisting("1234ABC", 1);
        assertFalse(result);
    }

    // Test that the testPlayer is actually added to the database
    // TODO: Fix this test (or maybe remove it entirely?)
    /*@Test
    void playerShouldBeAdded() {
        databaseConventions = new DatabaseConventions();
        int leaderboardId = 1;

        int score = 1337;
        String id = testPlayer.getId();
        String name = testPlayer.getName();
        String region = testPlayer.getRegion();
        String timestamp = testPlayer.getCreationDate();

        Transaction transaction = jedisMock.multi();

        transaction.zadd(databaseConventions.leaderboardSortedKeyString(leaderboardId), score, databaseConventions.leaderboardScoreKeyString(score, timestamp, id));
        transaction.hset(databaseConventions.leaderboardHashMapKeyString(leaderboardId), id, databaseConventions.leaderboardScoreKeyString(score, timestamp, id));

        // Add player as individual HashMap
        Map<String, String> fields = new HashMap<>();
        fields.put("id", id);
        fields.put("name", name);
        fields.put("score", String.valueOf(score));
        fields.put("region", region);
        fields.put("creationDate", timestamp);
        fields.put("rank", null);
        transaction.hmset(databaseConventions.playerObjectKeyString(id), fields);

        // Map player's name to their ID
        transaction.hset("playerNames:" + leaderboardId, fields.get("name"), id);

        transaction.exec();

        //when(jedisMock.hexists("leaderboardSorted:1", "ABCD123")).thenReturn(true);
        boolean result = databaseService.isPlayerExisting("ABCD123", 1);
        assertTrue(result);

        // Remove testPlayer from database
        jedisMock.del("leaderboardSorted:1", "1337:" + timestamp + ":ABCD123");
        jedisMock.del("leaderboardHashMap:1", "ABCD123");
        jedisMock.del("player:ABCD123");
        jedisMock.hdel("playerNames:1", "Bruce Wayne");
    }*/
}