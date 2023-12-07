package application.services;

import application.models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseServiceTest {
    DatabaseService databaseService;
    JedisPool jedisPoolMock;
    Jedis jedisMock;
    Player testPlayer;

    @BeforeEach
    void setUp() {
        // Create mock objects using Mockito to control their behavior and isolate tested methods
        jedisPoolMock = Mockito.mock(JedisPool.class);
        jedisMock = Mockito.mock(Jedis.class);

        // Define the behavior of the mock objects
        when(jedisPoolMock.getResource()).thenReturn(jedisMock);

        databaseService = new DatabaseService();
        databaseService.setJedisPool(jedisPoolMock);

        testPlayer = new Player("ABCD123", "Bruce Wayne", "1337", "NA", "1587991691");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(jedisPoolMock, jedisMock);
        jedisPoolMock = null;
        jedisMock = null;
        databaseService = null;

        testPlayer = null;
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
}