package Backend.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlayerController {
    private DatabaseService databaseService;

    @Autowired // Autowired constructor to add Service to the RestController
    public PlayerController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Gets the players with the highest scores
    @GetMapping("/users")
    ResponseEntity<List<String>> Users(@RequestParam int min, int max) {
        return new ResponseEntity<>(databaseService.getPlayersByPoints(min, max), HttpStatus.OK);
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
    ResponseEntity<Integer> Size() {
        return new ResponseEntity<>(databaseService.getSize(), HttpStatus.OK);
    }

    @GetMapping("/scores")
    ResponseEntity<Integer> Scores(@RequestParam String player) {
        return new ResponseEntity<>(databaseService.getPointsByPlayers(player), HttpStatus.OK);
    }

    //Error
    @GetMapping("/error")
    String Error(){
        return "Bad luck, u fucked it up";
    }
}