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

import java.util.Set;

/**
 * Created by aaryno on 1/10/17.
 */
@AllArgsConstructor
@Slf4j
@Primary
@Repository
public class RedisSetRepositoryImpl implements RedisSetRepository {

    protected final RedisTemplate<String, Object> redisTemplate;

    @Override
    public long addToSet(String key, String value) {
        log.debug("addToSet key: " + key + " value: " + value);
        return redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public long removeFromSet(String key, String value) {
        log.debug("removeFromSet key: " + key + " value: " + value);
        return redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public Set<Object> getSetMembers(String key) {
        log.debug("Querying Set - KEY: " + key);
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Set<Object> getSetIntersection(String key1, String key2) {
        log.debug("querySetIntersection key1: " + key1 + " key2: " + key2);
        return redisTemplate.opsForSet().intersect(key1, key2);
        //Set<Object> key1Results = getSetMembers(key1);
        //Set<Object> key2Results = getSetMembers(key2);
        //key1Results.retainAll(key2Results);
        //return key1Results;
    }

    @Override
    public Set<Object> getSetUnion(String key1, String key2) {
        log.debug("querySetUnion key1: " + key1 + " key2: " + key2);
        return redisTemplate.opsForSet().union(key1, key2);
        //Set<Object> key1Results = getSetMembers(key1);
        //Set<Object> key2Results = getSetMembers(key2);
        //key1Results.addAll(key2Results);
        //return key1Results;
    }

}
