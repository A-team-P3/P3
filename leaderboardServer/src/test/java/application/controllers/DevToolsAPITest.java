package application.controllers;

import application.models.Player;
import application.services.DatabaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

public class DevToolsAPITest {

    DatabaseService databaseServiceMock;

    @BeforeEach
    void setup() {
        // Mock the DatabaseService
        databaseServiceMock = mock(DatabaseService.class);

        // Initialize the playerCaptor
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
        databaseServiceMock = null;
    }

    @Test
    void devToolsApiShouldExist() {
        DevToolsAPI devToolsAPI = new DevToolsAPI(databaseServiceMock);
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
}