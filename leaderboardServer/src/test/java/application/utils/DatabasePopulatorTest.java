package application.utils;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class DatabasePopulatorTest {

    @Test
    public void databasePopulatorShouldExist() {
        Jedis jedisMock = mock(Jedis.class);
        DatabasePopulator databasePopulator = new DatabasePopulator(jedisMock);
        assertNotNull(databasePopulator);
    }

    @Test
    public void databaseShouldBePopulated() {
        // Create mock objects simulating the behavior or Jedis and Transaction classes
        Jedis jedisMock = mock(Jedis.class);
        Transaction transactionMock = mock(Transaction.class);

        // Set up mock behavior: return mocked Transaction whenever multi() is called on mocked Jedis
        when(jedisMock.multi()).thenReturn(transactionMock);

        // Create instance of class to test
        DatabasePopulator databasePopulator = new DatabasePopulator(jedisMock);

        // Call method to test
        databasePopulator.populateDatabase(42, 3);

        // Verify that the correct number of interactions with the mocked Jedis and Transaction
        verify(jedisMock, times(1)).multi();
        verify(transactionMock, times(3)).zadd(anyString(), anyDouble(), anyString());
        verify(transactionMock, times((3 * 2))).hset(anyString(), anyString(), anyString());
        verify(transactionMock, times(3)).hmset(anyString(), any(Map.class));
        verify(transactionMock, times(1)).exec();
    }
}