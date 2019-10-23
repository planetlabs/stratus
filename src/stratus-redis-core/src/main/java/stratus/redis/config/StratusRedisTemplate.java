/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Custom RedisTemplate which uses {@link StratusHashOperations}
 *
 * Created by tbarsballe on 2016-12-13.
 */
public class StratusRedisTemplate<K, V> extends RedisTemplate<K, V> {

    @Override
    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return new StratusHashOperations<>(this, super.opsForHash());
    }
}
