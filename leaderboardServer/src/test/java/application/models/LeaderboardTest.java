package application.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// TODO: set 'runTests = true' before running tests! Location: leaderboardServer/gradle.properties

class LeaderboardTest {
    Leaderboard leaderboard;

    @BeforeEach
    void setUp() {
        leaderboard = new Leaderboard(1, "TestLeaderboard", 42, null);
    }

    @AfterEach
    void tearDown() {
        leaderboard = null;
    }

    void leaderboardShouldExist() {
        assertNotNull(leaderboard);
    }
}