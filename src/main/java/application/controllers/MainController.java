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
    @GetMapping("/error")
    String error(){
        return "Bad luck, u fucked it up";
    }
}