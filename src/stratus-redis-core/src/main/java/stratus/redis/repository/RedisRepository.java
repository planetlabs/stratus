/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.repository;

import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author joshfix
 */
public interface RedisRepository {

    RedisValueRepository getRedisValueRepository();

    void setRedisValueRepository(RedisValueRepository redisValueRepository);

    RedisSetRepository getRedisSetRepository();

    void setRedisSetRepository(RedisSetRepository redisSetRepository);

    RedisHashRepository getRedisHashRepository();

    void setRedisHashRepository(RedisHashRepository redisHashRepository);

    void deleteKey(String key);
    
    void renameKey(String key, String newKey);
    
    void deleteKeys(Collection<String> key);
    
    boolean keyExists(String key);
    
    DataType type(String key);
    
    RedisTemplate getRedisTemplate();

    void flush();

    List<String> scanKeys(String pattern);

    void setGeoServerInitialized(boolean init);

    boolean isGeoServerInitialized();

    void setStoreInitialized(boolean init);

    boolean getStoreInitialized();

    boolean isReadOnly();
}
