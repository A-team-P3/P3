package application.controllers;

import application.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameInputAPI {
    private DatabaseService databaseService;

    @Autowired // Autowired constructor
    // to add Service to the RestController
    public GameInputAPI(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
}