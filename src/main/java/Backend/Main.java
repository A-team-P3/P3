package Backend;

import Backend.API.DatabaseService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

@SpringBootApplication // Tells Spring that this is the main class
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        // Connect to Redis Cloud database
        Jedis jedis = DatabaseService.getJedis();
        jedis.auth("MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9");


    }
}