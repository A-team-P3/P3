package Backend.API;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;
import org.springframework.stereotype.Service;


@Service
public class DBservice {

    private JedisPool jedisPool;

    public DBservice() {
        // Assuming you want to configure the pool in the constructor,
        // otherwise you could move this to an @PostConstruct method or a configuration class.
        this.jedisPool = new JedisPool("redis-12618.c304.europe-west1-2.gce.cloud.redislabs.com", 12618); // Add your host and port here
    }

    private Jedis getJedisConnection() {
        Jedis jedis = jedisPool.getResource();
        jedis.auth("MdgWuJDGsrEQiRjP8rNawQNQ9Cls2Qp9"); // Add your Redis password here
        return jedis;
    }

    public String setValue(String key, String value) {
        try (Jedis jedis = getJedisConnection()) {
            return jedis.set(key, value);
        } catch (JedisException e) {
            // handle the exception properly
            throw e;
        }
    }

    public String getValue(String key) {
        try (Jedis jedis = getJedisConnection()) {
            return jedis.get(key);
        } catch (JedisException e) {
            // handle the exception properly
            throw e;
        }
    }

    // Add more methods for other Redis operations as needed.
}