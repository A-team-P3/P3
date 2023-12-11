package application.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HTMLController {

    @GetMapping("/dev")
    public String leaderboardManager() {
        return "leaderboardManager.html";
    }

    @GetMapping("/error")
    public String error() {
        return "Whoops!";
    }
}