package Backend.API;

import org.springframework.stereotype.Controller;

@Controller
public class WebController {
    public String index() {
        return "index";
    }
}
