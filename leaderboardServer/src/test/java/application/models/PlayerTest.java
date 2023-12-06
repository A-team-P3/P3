package application.models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// TODO: set 'runTests = true' before running tests! Location: leaderboardServer/gradle.properties

class PlayerTest {
    Player player, player2, player3;

    @BeforeEach
    void setUp() {
        player = new Player("A1B2C3D", "Tester", "1337", "NA");
        player2 = new Player("ABCD123", "Tester2", "42", "EU", "2022-08-29");
        player3 = new Player("1A2B3C4", "Tester3", "69", "AS", "2023-12-24", "314");
    }

    @AfterEach
    void tearDown() {
        player = null;
        player2 = null;
        player3 = null;
    }

    @Test
    void playerShouldExist() {
        assertNotNull(player);
    }

    @Test
    void player2ShouldExist() {
        assertNotNull(player2);
    }

    @Test
    void player3ShouldExist() {
        assertNotNull(player3);
    }

    @Test
    void idShouldBeABCD123() {
        assertEquals("A1B2C3D", player.getId());
    }

    @Test
    void nameShouldBeTester() {
        assertEquals("Tester", player.getName());
    }

    @Test
    void scoreShouldBe42() {
        assertEquals("1337", player.getScore());
    }

    @Test
    void regionShouldBeEU() {
        assertEquals("NA", player.getRegion());
    }

    @Test
    void rankShouldBe69() {
        assertEquals("314", player3.getRank());
    }

    @Test
    void creationDateShouldBeSetByDefault() {
        assertNotNull(player.getCreationDate());
    }

    @Test
    void creationDateShouldBeCorrect() {
        assertEquals(player3.getCreationDate(), "2023-12-24");
    }
}