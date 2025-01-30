package com.data.validation.redis;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;

@Getter
@Setter
public class RedisCacheManager {

    private Jedis jedis;

    /**
     * Method used to establish connection to Redis
     * @param host Redis host
     * @param port Redis port
     */
    public RedisCacheManager(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    /**
     * Method used to retrieve data from Redis
     * @param key Redis key
     * @return data recovered
     */
    public String getFromCache(String key) {
        return jedis.get(key);
    }

    /**
     * Method used to save data to Redis
     * @param key Redis key
     * @param value Redis value
     */
    public void putInCache(String key, String value) {
        jedis.set(key, value);
    }

    /**
     * Method used to delete data on Redis
     * @param key Redis key
     */
    public void clearCache(String key) {
        jedis.del(key);
    }

    /**
     * Method used to close the connection to Redis
     */
    public void close() {
        jedis.close();
    }
}
