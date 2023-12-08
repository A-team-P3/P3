package application.controllers;

import application.models.Leaderboard;
import application.models.Player;
import application.models.PlayerScore;
import application.services.DatabaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class DevToolsAPITest {

    DevToolsAPI devToolsAPI;
    DatabaseService databaseServiceMock;

    @BeforeEach
    void setup() {
        // Mock the DatabaseService
        databaseServiceMock = mock(DatabaseService.class);
        devToolsAPI = new DevToolsAPI(databaseServiceMock);

        // Initialize the playerCaptor
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        databaseServiceMock = null;
        devToolsAPI = null;
    }

    @Test
    void devToolsApiShouldExist() {
        assertNotNull(devToolsAPI);
    }

    @Captor
    // Used to capture the Player object that is passed to setPlayerObject
    ArgumentCaptor<Player> playerCaptor;

    @Test
    void setPlayerObjectEndPointShouldReturnCorrectTestId() {
        // Create an instance of DevToolsAPI
        DevToolsAPI devToolsAPI = new DevToolsAPI(databaseServiceMock);

        // Define the input parameters for the setPlayerObject method
        String id = "TestID";
        String name = "TestName";
        String score = "69";
        String region = "EU";

        // Define the expected response
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Player created with ID: " + id);

        // Call the method to test
        ResponseEntity<String> actualResponse = devToolsAPI.setPlayerObject(id, name, score, region);

        // Capture the Player object that was passed to setPlayerObject
        verify(databaseServiceMock).setPlayerObject(playerCaptor.capture());

        // Assert that the fields of the captured Player object have the expected values
        Player capturedPlayer = playerCaptor.getValue();
        assertEquals(id, capturedPlayer.getId());
        assertEquals(name, capturedPlayer.getName());
        assertEquals(score, capturedPlayer.getScore());
        assertEquals(region, capturedPlayer.getRegion());

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getPlayerObjectEndPointShouldReturnCorrectPlayer() {
        // Input argument
        String id = "TestID";

        Player expectedPlayer = new Player(id, "TestName", "69", "EU");

        // Define the expected response
        ResponseEntity<Player> expectedResponse = new ResponseEntity<>(expectedPlayer, HttpStatus.OK);

        // Mock the behavior of databaseService.getPlayerObject
        when(databaseServiceMock.getPlayerObject(id)).thenReturn(expectedPlayer);

        // Call getPlayerObject with the input argument
        ResponseEntity<Player> actualResponse = devToolsAPI.getPlayerObject(id);

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getScoresByRangeEndPointShouldReturnCorrectLeaderboard() throws Exception {
        // Arrange
        int leaderboardId = 1;
        int start = 0;
        int stop = 2;
        List<PlayerScore> playerScores = List.of(
                new PlayerScore(42, "TestName1", "EU", 1),
                new PlayerScore(69, "TestName2", "NA", 2),
                new PlayerScore(1337, "TestName3", "SA", 3)
        );

        // Define the expectations
        Leaderboard expectedLeaderboard = new Leaderboard(1, "TestLeaderboard", 3, playerScores);
        ResponseEntity<Leaderboard> expectedResponse = new ResponseEntity<>(expectedLeaderboard, HttpStatus.OK);

        // Mock the behavior
        when(databaseServiceMock.getScoresByRange(leaderboardId, start, stop)).thenReturn(expectedLeaderboard);

        // Call getScoresByRange with the input arguments
        ResponseEntity<Leaderboard> actualResponse = devToolsAPI.getScoresByRange(leaderboardId, start, stop);

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void addScoreEndPointShouldReturnCorrectResponse() throws Exception {
        // Arrange
        int leaderboardId = 1;
        int score = 42;
        String playerId = "TestID";

        // Expected response
        String expectedResponse = "Score " + score + " added to leaderboard " + leaderboardId + " for player " + playerId;

        // Mock behavior
        when(databaseServiceMock.setScore(playerId, score, leaderboardId)).thenReturn(expectedResponse);

        // Call addScoreToLeaderboard with the input arguments
        String actualResponse = devToolsAPI.addScoreToLeaderboard(leaderboardId, score, playerId);

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void populateDatabaseEndPointShouldReturnCorrectResponse() throws Exception {
        int leaderboardId = 1;
        int numberOfScores = 10;

        // Define the expected response
        ResponseEntity<String> expectedResponse = new ResponseEntity<>(numberOfScores + "scores should be created now", HttpStatus.OK);

        // Mock the behavior. doNothing() is used to mock a void method.
        doNothing().when(databaseServiceMock).populateLeaderboard(leaderboardId, numberOfScores);

        // Call populateDatabase() with the input arguments
        ResponseEntity<String> actualResponse = devToolsAPI.populateDatabase(leaderboardId, numberOfScores);

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void wipeLeaderboardEndPointShouldReturnCorrectResponse() {
        int leaderboardId = 1;

        // Define the expected response
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Leaderboard " + leaderboardId + " wiped!", HttpStatus.OK);

        // Mock the behavior. doNothing() is used to mock a void method.
        doNothing().when(databaseServiceMock).wipeLeaderboard(leaderboardId);

        // Call wipeLeaderboard() with the input argument
        ResponseEntity<String> actualResponse = devToolsAPI.wipeLeaderboard(leaderboardId);

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void wipeDatabaseEndPointShouldReturnCorrectResponse() {
        // Define the expected response
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Entire database wiped!", HttpStatus.OK);

        // Mock the behavior. doNothing() is used to mock a void method.
        doNothing().when(databaseServiceMock).wipeDatabase();

        // Call wipeDatabase()
        ResponseEntity<String> actualResponse = devToolsAPI.wipeDatabase();

        // Assert that the actual response matches the expected response
        assertEquals(expectedResponse, actualResponse);
    }
}