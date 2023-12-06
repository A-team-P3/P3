package application.controllers;

import application.models.Leaderboard;
import application.models.Player;
import application.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DevToolsAPI {
    private DatabaseService databaseService;

    @Autowired // Autowired constructor
    // to add Service to the RestController
    public DevToolsAPI(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/setPlayerObject")
    public ResponseEntity<String> setPlayerObject(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String score,
            @RequestParam String region) {

        Player player = new Player(id, name, score, region);
        databaseService.setPlayerObject(player);

        return ResponseEntity.ok("Player created with ID: " + id);
    }

    @GetMapping("/getPlayerObject")
    public ResponseEntity<Player> getPlayerObject(@RequestParam String id) {
        Player player = databaseService.getPlayerObject(id);
        if (player == null) {
            return new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Player>(player, HttpStatus.OK);
    }

    @GetMapping("/getScoresByRange")
    ResponseEntity<Leaderboard> getScoresByRange(
            @RequestParam int leaderboardId,
            @RequestParam int start,
            @RequestParam int stop) throws Exception {
        return new ResponseEntity<Leaderboard>(databaseService.getScoresByRange(leaderboardId, start, stop), HttpStatus.OK);
    }

    @GetMapping("/addScoreToLeaderboard")
    String addScoreToLeaderboard(
            @RequestParam int leaderboardId,
            @RequestParam int score,
            @RequestParam String playerId) throws Exception {
        return databaseService.setScore(playerId, score, leaderboardId);
    }
    @GetMapping("/populateDatabase")
    ResponseEntity<String> populateDatabase(
            @RequestParam int leaderboardId,
            @RequestParam int numberOfScores) throws Exception {
        databaseService.populateLeaderboard(leaderboardId, numberOfScores);
        return new ResponseEntity<>(numberOfScores + "scores should be created now", HttpStatus.OK);
    }

    // Wipe specified leaderboard
    @GetMapping("/wipeLeaderboard")
    ResponseEntity<String> wipeLeaderboard(@RequestParam int leaderboardId) {
        databaseService.wipeLeaderboard(leaderboardId);
        return new ResponseEntity<>("Leaderboard " + leaderboardId + " wiped!", HttpStatus.OK);
    }

    // Wipe entire database
    @GetMapping("/wipeDatabase")
    ResponseEntity<String> wipeDatabase() {
        databaseService.wipeDatabase();
        return new ResponseEntity<>("Entire databases wiped!", HttpStatus.OK);
    }
}