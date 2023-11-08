package Backend.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // This means that this class is a RestController
@RequestMapping("/") // This means URL's start with / (after Application path)
public class PlayerController {
    private final DBService dbService;

    @Autowired // Autowired constructor to add Service to the RestController
    public PlayerController(DBService dbService) {
        this.dbService = dbService;
    }

    @GetMapping("/createLeaderboard")
    ResponseEntity<String> createLeaderboard(@RequestParam int leaderboardId) throws Exception {
        return dbService.createLeaderboard(leaderboardId);
    }
    @GetMapping("/addScoreToLeaderboard")
    ResponseEntity<String> addScoreToLeaderboard(@RequestParam int leaderboardId, @RequestParam int score, @RequestParam String userId) throws Exception {
        return dbService.addScoreToLeaderboard(leaderboardId, score, userId);
    }
    @GetMapping("/populateDatabase")
    ResponseEntity<String> populateDatabase(@RequestParam int leaderboardId, @RequestParam int numberOfUsers) throws Exception {
        return dbService.populateDatabase(leaderboardId, numberOfUsers);
    }
    @GetMapping("/findByUserId")
    ResponseEntity<String> findUserById(@RequestParam int leaderboardId, @RequestParam String userId) throws Exception {
        return dbService.findByUserId(leaderboardId, userId);
    }
    @GetMapping("/getScoresByRange")
    ResponseEntity<String> getScoresByRange(@RequestParam int leaderboardId, @RequestParam int start, @RequestParam int stop) throws Exception {
        return dbService.getScoresByRange(leaderboardId, start, stop);
    }
}
