package com.data.validation.redis;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;

@Getter
@Setter
public class RedisCacheManager {

    private Jedis jedis;

    /**
     * Constructor to create a new instance of RedisCacheManager and establish a connection to the Redis server.
     * @param host the host address of the Redis server
     * @param port the port number on which the Redis server is running
     */
    public RedisCacheManager(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    /**
     * Retrieves a value from the Redis cache for the specified key.
     * @param key the key used to fetch data from the Redis cache
     * @return the value associated with the given key as a String, or null if the key does not exist or an error occurs
     */
    public String getFromCache(String key) {
        return jedis.get(key);
    }

    /**
     * Saves a key-value pair in the Redis cache.
     * @param key the key under which the value will be stored in the Redis cache
     * @param value the value to be stored in the Redis cache
     */
    public void putInCache(String key, String value) {
        jedis.set(key, value);
    }

    /**
     * Removes the specified key from the Redis cache.
     * @param key the key to be removed from the Redis cache
     */
    public void clearCache(String key) {
        jedis.del(key);
    }

    /**
     * Clears all cached data in the Redis server.
     */
    public void clearAllCache() {
        jedis.flushAll();
    }

    /**
     * Closes the connection to the Redis server managed by this instance of {@code RedisCacheManager}.
     * This method ensures that all resources associated with the Redis connection are released.
     */
    public void close() {
        jedis.close();
    }
}
