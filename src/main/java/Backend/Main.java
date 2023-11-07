package Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;

@SpringBootApplication // Tells Spring that this is the main class
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        // Connect to Redis Cloud database
        Jedis jedis = new Jedis("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618);
        jedis.auth("MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9");

        // Print out the top 6 players (test)
        jedis.zrevrange("playersByPoints", 0, 5).forEach(System.out::println);
    }
}