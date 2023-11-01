package Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Q: what does this annotation do? A: it tells Spring that this is the main class
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }



}
