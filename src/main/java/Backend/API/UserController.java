package Backend.API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    private UserService userService;
    @Autowired //Autowired constructor to add Service to the RestController
    public UserController(UserService userService) {
        this.userService = userService;
    }

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

    //test path
    @GetMapping("/users")
    ResponseEntity<List<User>> Users() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i< userService.GetSize(); i++) {
            users.add(userService.GetUser(i));
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
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
    ResponseEntity<Integer> Size(){
        return new ResponseEntity<>(userService.GetSize(), HttpStatus.OK);
    }
}
