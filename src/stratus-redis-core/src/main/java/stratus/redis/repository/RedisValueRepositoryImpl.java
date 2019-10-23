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

/**
 * Created by aaryno on 1/10/17.
 */
@AllArgsConstructor
@Slf4j
@Primary
@Repository
public class RedisValueRepositoryImpl implements RedisValueRepository {

    protected final RedisTemplate<String, Object> redisTemplate;

    @Override
    public String getValue(String key) {
        log.debug("Querying Value - KEY: " + key);
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setValue(String key, String value) {
        log.debug("setValue key: " + key + " value: " + value);
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }


    public String getSetValue(String key, String value) {
        log.debug("setValue key: " + key + " value: " + value);
        return (String)redisTemplate.opsForValue().getAndSet(key, value);
    }



}
