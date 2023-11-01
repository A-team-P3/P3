package Backend.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/hello") // Tells Spring that this is a GET endpoint
    ResponseEntity<String> hello(@RequestParam int n) {
        if (n > 0) {
            return new ResponseEntity<>("parameter is larger then 0", HttpStatus.OK);
        }

        if (n == 0) {
            return new ResponseEntity<>("parameter is 0", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    getUsersByRank(int min, int max, String region)
        list of users

    getUserByName(String name)
        index of user

    getUserByScore(int score)
        index of user

    getNumberOfPlayers()
        number of players
     */
}
