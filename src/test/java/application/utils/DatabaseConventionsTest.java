package application.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConventionsTest {

    @Test
    void leaderboardSortedKeyString() {
        DatabaseConventions databaseConventions = new DatabaseConventions();
        assertEquals("leaderboardSorted:1", databaseConventions.leaderboardSortedKeyString(1));
    }

    @Test
    void leaderboardHashMapKeyString() {
        DatabaseConventions databaseConventions = new DatabaseConventions();
        assertEquals("leaderboardHashMap:1", databaseConventions.leaderboardHashMapKeyString(1));
    }

    @Test
    void leaderboardScoreKeyString() {
        DatabaseConventions databaseConventions = new DatabaseConventions();
        assertEquals("1337:TestTimestamp:TestID", databaseConventions.leaderboardScoreKeyString(1337, "TestTimestamp", "TestID"));
    }

    @Test
    void playerObjectKeyString() {
        DatabaseConventions databaseConventions = new DatabaseConventions();
        assertEquals("player:42", databaseConventions.playerObjectKeyString("42"));
    }
}