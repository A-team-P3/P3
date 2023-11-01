package Backend.API;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/hello")
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
}
