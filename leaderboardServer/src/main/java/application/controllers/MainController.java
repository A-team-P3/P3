package application.controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import application.models.Player;
import application.services.DatabaseService;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.resps.Tuple;

import java.io.IOException;
import java.util.List;

@RestController
public class MainController {
    private DatabaseService databaseService;

    @Autowired // Autowired constructor
    // to add Service to the RestController
    public MainController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Gets the players with the highest scores
    @GetMapping("/players")
    ResponseEntity<List<Tuple>> players(@RequestParam int leaderboardId, int min, int max) {
        return new ResponseEntity<>(databaseService.getMembersByRange(leaderboardId, min, max), HttpStatus.OK);
    }

    @PostMapping("/setScore")
    ResponseEntity<String> setScore(@RequestParam String playerId, int newScore, int leaderboardId) {
        // Sends back a string with the new score
        return new ResponseEntity<>(databaseService.setScore(playerId, newScore, leaderboardId), HttpStatus.OK);
    }
    /*
    getUsersByRank(int min, int max, String region)
        list of users

    getUserByName(String name)
        index of user

    getUserByScore(int score)
        index of user

    getNumberOfPlayers() //called it size
        number of players
     */
    @GetMapping("/size")
    ResponseEntity<Integer> size(@RequestParam int leaderboardId) {
        return new ResponseEntity<>(databaseService.getSize(leaderboardId), HttpStatus.OK);
    }


    //Error

    @GetMapping("/createPlayer")
    public ResponseEntity<String> createPlayer(
            @RequestParam String id,
            @RequestParam String name,
            @RequestParam String region) {

        Player player = new Player(id, name, region);
        databaseService.setPlayer(player);

        return ResponseEntity.ok("Player created with ID: " + id);

    }
    @GetMapping("/getPlayer")
    public ResponseEntity<String> getPlayer(@RequestParam String id) {
        Player player = databaseService.getPlayer(id);
        String response = "id =" + player.getId() + "name = " + player.getName() + "region = " + player.getRegion() + "creation date =" + player.getCreationDate();
        return new ResponseEntity<String>(response, HttpStatus.OK);

    }
    //@GetMapping for showing leaderboardManager.html

    /* Leaderboard manager API
    @GetMapping("/createLeaderboard")
    ResponseEntity<String> createLeaderboard(@RequestParam int leaderboardId) throws Exception {
        return databaseService.createLeaderboard(leaderboardId);
    }
    @GetMapping("/addScoreToLeaderboard")
    ResponseEntity<String> addScoreToLeaderboard(@RequestParam int leaderboardId, @RequestParam int score, @RequestParam String userId) throws Exception {
        return databaseService.addScoreToLeaderboard(leaderboardId, score, userId);
    }
    @GetMapping("/populateDatabase")
    ResponseEntity<String> populateDatabase(@RequestParam int leaderboardId, @RequestParam int numberOfUsers) throws Exception {
        return databaseService.populateDatabase(leaderboardId, numberOfUsers);
    }
    @GetMapping("/findByUserId")
    ResponseEntity<String> findUserById(@RequestParam int leaderboardId, @RequestParam String userId) throws Exception {
        return databaseService.findByUserId(leaderboardId, userId);
    }
    @GetMapping("/getScoresByRange")
    ResponseEntity<String> getScoresByRange(@RequestParam int leaderboardId, @RequestParam int start, @RequestParam int stop) throws Exception {
        return databaseService.getScoresByRange(leaderboardId, start, stop);
        */
}
