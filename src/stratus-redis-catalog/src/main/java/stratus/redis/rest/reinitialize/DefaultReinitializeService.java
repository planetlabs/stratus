/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.reinitialize;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import stratus.redis.geoserver.RedisGeoServerLoader;

import static stratus.redis.geoserver.RedisGeoServerLoader.GEOSERVER_INITIALIZATION_KEY;

/**
 * @author joshfix
 * Created on 6/15/18
 */
@Service
@AllArgsConstructor
public class DefaultReinitializeService implements ReinitializeService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisGeoServerLoader redisGeoServerLoader;

    @Override
    public void reinitialize() {
        redisTemplate.delete(GEOSERVER_INITIALIZATION_KEY);
        redisGeoServerLoader.initializeGeoServerSubSystem();
        redisGeoServerLoader.setGeoServerInitialized();
    }
}
