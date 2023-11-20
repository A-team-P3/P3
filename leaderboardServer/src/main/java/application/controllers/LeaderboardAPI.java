package application.controllers;

import application.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.resps.Tuple;

import java.util.List;

@RestController
public class LeaderboardAPI {
    private final DatabaseService databaseService;

    @Autowired // Autowired constructor
    // to add Service to the RestController
    public LeaderboardAPI(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    @GetMapping("/size")
    ResponseEntity<Integer> size(@RequestParam int leaderboardId) {
        return new ResponseEntity<>(databaseService.getSize(leaderboardId), HttpStatus.OK);
    }
    @GetMapping("/players")
    ResponseEntity<List<Tuple>> players(@RequestParam int leaderboardId, int start, int stop) {
        return new ResponseEntity<>(databaseService.getMembersByRange(leaderboardId, start, stop), HttpStatus.OK);
    }
}

