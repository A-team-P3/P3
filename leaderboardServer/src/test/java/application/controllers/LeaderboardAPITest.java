package application.controllers;

import application.models.Player;
import application.services.DatabaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Auto-configures a MockMvc instance, used for Spring Boot tests,
// and only loads the web layer context of the LeaderboardAPI class.
@WebMvcTest(LeaderboardAPI.class)
class LeaderboardAPITest {
    Player player, player2;

    @BeforeEach
    void setUp() {
        player = new Player("ABCD123", "Bruce Wayne", "1337", "NA", "2023-12-24", "1");
        player2 = new Player("1234ABC", "Robin", "42", "NA", "2024-01-01", "2");
    }

    @AfterEach
    void tearDown() {
        player = null;
    }

    // This annotation injects a MockMvc instance for testing HTTP layer,
    // enabling sending HTTP requests and asserting responses.
    @Autowired
    private MockMvc mockMvc;

    // @MockBean is used to create a mock instance of DatabaseService for testing, isolating the
    // LeaderboardAPI from actual database interactions.
    @MockBean
    private DatabaseService databaseService;

    // /findPlayer end-point
    // Simulate a GET request to the end-point, assert a 200 (OK) status code and expected JSON response.
    @Test
    void findPlayerShouldReturnMatchingPlayer() throws Exception {
        // Arrange
        String inputName = "Bruce Wayne";
        int leaderboardId = 1;
        List<Player> matchingPlayers = List.of(player);

        // Act
        // Mock findPlayersByName() with when() from the Mockito library
        // This isolates the unit of code being tested, removing dependencies and allowing control over external
        // system behavior. This ensures tests are reliable, fast and focused on the unit's behavior.
        when(databaseService.findPlayersByName(inputName, leaderboardId)).thenReturn(matchingPlayers);

        // Assert
        // Simulate the HTTP GET request to the /findPlayer end-point
        mockMvc.perform(MockMvcRequestBuilders.get("/findPlayer")   // Building the request
            .param("name", inputName)
            .param("leaderboardId", String.valueOf(leaderboardId))
            .accept(MediaType.APPLICATION_JSON))                              // Accepting JSON response
            .andExpect(status().isOk())                                       // Asserting the 200 (OK) status code
            .andExpect(content().json("[{" +                         // Asserting the expected JSON response
                    "'id':'ABCD123'," +
                    "'name':'Bruce Wayne'," +
                    "'score':'1337'," +
                    "'region':'NA'," +
                    "'creationDate':'2023-12-24', " +
                    "'rank':'1'" +
            "}]"));
    }

    // /players end-point
    @Test
    void playersShouldReturnExpectedResponse() throws Exception {
        // Arrange
        int leaderboardId = 1;
        int start = 0;
        int stop = 10;
        List<Player> players = List.of(player, player2);

        // Act
        when(databaseService.getMembersByRange(leaderboardId, start, stop)).thenReturn(players);

        // Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/players")
            .param("leaderboardId", String.valueOf(leaderboardId))
            .param("start", String.valueOf(start))
            .param("stop", String.valueOf(stop))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().json("[{" +
                "'id':'ABCD123'," +
                "'name':'Bruce Wayne'," +
                "'score':'1337'," +
                "'region':'NA'," +
                "'creationDate':'2023-12-24'," +
                "'rank':'1'" +
            "}, {" +
                "'id':'1234ABC'," +
                "'name':'Robin'," +
                "'score':'42'," +
                "'region':'NA'," +
                "'creationDate':'2024-01-01'," +
                "'rank':'2'" +
            "}]"));
    }
}