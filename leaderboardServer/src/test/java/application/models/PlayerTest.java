package application.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// TODO: set 'runTests = true' before running tests! Location: leaderboardServer/gradle.properties

class PlayerTest {
    Player player;

    @BeforeEach
    void setUp() {
        player = new Player("ABCD123", "Tester", "42", "EU", "69");
    }

    @AfterEach
    void tearDown() {
        player = null;
    }

    @Test
    void playerShouldExist() {
        assertNotNull(player);
    }

    @Test
    void idShouldBeABCD123() {
        assertEquals("ABCD123", player.getId());
    }

    @Test
    void nameShouldBeTester() {
        assertEquals("Tester", player.getName());
    }

    @Test
    void scoreShouldBe42() {
        assertEquals("42", player.getScore());
    }

    @Test
    void regionShouldBeEU() {
        assertEquals("EU", player.getRegion());
    }

    @Test
    void rankShouldBe69() {
        assertEquals("69", player.getRank());
    }

    @Test
    void rankShouldBeSetTo1337() {
        player.setRank("1337");
        assertEquals("1337", player.getRank());
    }

    @Test
    void creationDateShouldBeSetByDefault() {
        assertNotNull(player.getCreationDate());
    }

    @Test
    void creationDateShouldBeNow() {
        assertEquals(player.getCreationDate(), LocalDate.now());
    }
}