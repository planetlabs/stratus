/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by aaryno on 1/10/17.
 */
@AllArgsConstructor
@Slf4j
@Primary
@Repository
public class RedisHashRepositoryImpl implements RedisHashRepository{

    protected final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setHash(String key, String id, Object info) {
        log.debug("Saving Hash - KEY:" + key + " ID: "+ id);
        redisTemplate.opsForHash().put(key, id, info);
    }

    @Override
    public void deleteHash(String key, String id) {
        log.debug("deleteHash key: " + key + " id: " + id);
        redisTemplate.opsForHash().delete(key, id);
    }

    @Override
    public Object getHashById(String key, String id) {
        log.debug("Querying Hash - KEY: " + key + " ID: " + id);
        return redisTemplate.opsForHash().get(key, id);
    }

    @Override
    public Map<Object, Object> getHash(String key) {
        log.debug("queryHash key: " + key);
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public int hashLength(String key) {
        return redisTemplate.opsForHash().size(key).intValue();
    }
}
