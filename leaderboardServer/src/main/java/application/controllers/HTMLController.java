package application.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class HTMLController {
    /*
    public String index() {
        return "index";
    }
*/
    @GetMapping("/dev")
    public String leaderboardManager() {
        return "leaderboardManager.html";
    }
    @GetMapping("/error")
    String error() {
        return "Bad luck, u fucked it up";
    }


}