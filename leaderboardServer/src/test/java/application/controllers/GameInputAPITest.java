package application.controllers;

import application.services.DatabaseService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameInputAPITest {

    @Test
    public void gameInputApiShouldExist() {
        // Create a mock DatabaseService
        DatabaseService databaseService = mock(DatabaseService.class);

        GameInputAPI gameInputAPI = new GameInputAPI(databaseService);
        assertNotNull(gameInputAPI);
    }
}