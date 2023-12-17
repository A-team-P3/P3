package application.controllers;

import application.models.Player;
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

    @Autowired // Autowired constructor to add Service to the RestController
    public LeaderboardAPI(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/size")
    ResponseEntity<Integer> size(@RequestParam int leaderboardId) {
        return new ResponseEntity<>(databaseService.getSize(leaderboardId), HttpStatus.OK);
    }

    //End-point to get a list of players
    @GetMapping("/players")
    ResponseEntity<List<Player>> players(
            @RequestParam int leaderboardId,
            @RequestParam int start,
            @RequestParam int stop) {
        return new ResponseEntity<>(databaseService.getMembersByRange(leaderboardId, start, stop), HttpStatus.OK);
    }

    @GetMapping("/leaderboards")
    ResponseEntity<List<Integer>> leaderboards() {
        return new ResponseEntity<>(databaseService.getLeaderboardAmount(), HttpStatus.OK);
    }

    // End-point to find player(s) by name
    @GetMapping("/findPlayer")
    ResponseEntity<List<Player>> findPlayer(
            @RequestParam int leaderboardId,
            @RequestParam String name) {
        try {
            List<Player> matchingPlayers = databaseService.findPlayersByName(name, leaderboardId);
            return new ResponseEntity<>(matchingPlayers, HttpStatus.OK);
        }
        catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}