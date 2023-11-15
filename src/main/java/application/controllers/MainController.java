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
    @GetMapping("/users")
    ResponseEntity<List<Tuple>> Users(@RequestParam int leaderboardId, int min, int max) {
        return new ResponseEntity<>(databaseService.getMembersByRange(leaderboardId, min, max), HttpStatus.OK);
    }
//remove this
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
    ResponseEntity<Integer> Size() {
        return new ResponseEntity<>(databaseService.getSize(), HttpStatus.OK);
    }


    //Error
    @GetMapping("/error")
    String Error(){
        return "Bad luck, u fucked it up";
    }
}