package application.services;

import application.models.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseServiceTest {
    DatabaseService databaseServiceMock;
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

        // Create partial mock of the DatabaseService class, allowing us to override the behavior of the getJedisConnection method
        databaseServiceMock = Mockito.spy(new DatabaseService());
        databaseServiceMock.setJedisPool(jedisPoolMock);

        testPlayer = new Player("ABCD123", "Bruce Wayne", "1337", "NA", "1587991691");
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(jedisPoolMock, jedisMock);
        jedisPoolMock = null;
        jedisMock = null;
        databaseServiceMock = null;

        testPlayer = null;
    }

    @Test
    void databaseServiceShouldExist() {
        assertNotNull(databaseServiceMock);
    }

    @Test
    void jedisConnectionShouldBeAlive() {
        Jedis result = databaseServiceMock.getJedisConnection();
        assertNotNull(result);

        // Verify that the mock objects were used as expected
        Mockito.verify(jedisPoolMock).getResource();
    }

    @Test
    void sizeShouldBe42() {
        when(jedisMock.zcard("leaderboardSorted:1")).thenReturn(42L);
        Integer result = databaseServiceMock.getSize(1);
        assertEquals(42, result);
    }

    @Test
    void playerShouldExist() {
        when(jedisMock.hexists("leaderboardSorted:1", "ABCD123")).thenReturn(true);
        boolean result = databaseServiceMock.isPlayerExisting("ABCD123", 1);
        assertTrue(result);
    }

    @Test
    void playerShouldNotExist() {
        when(jedisMock.hexists("leaderboardSorted:1", "1234ABC")).thenReturn(false);
        boolean result = databaseServiceMock.isPlayerExisting("1234ABC", 1);
        assertFalse(result);
    }

    @Test
    void getLeaderboardAmountShouldReturnThreeLeaderboards() {
        // Arrange
        Jedis jedisMock = Mockito.mock(Jedis.class);
        Set<String> keys = new HashSet<>(Arrays.asList("leaderboardSorted:1", "leaderboardSorted:2", "leaderboardSorted:42"));
        List<Integer> expectedAmount = Arrays.asList(1, 2, 42);

        // Act
        when(databaseServiceMock.getJedisConnection()).thenReturn(jedisMock);
        when(jedisMock.keys("leaderboardSorted:*")).thenReturn(keys);

        // Assert
        List<Integer> actualAmount = databaseServiceMock.getLeaderboardAmount();
        assertEquals(expectedAmount, actualAmount);
    }
}