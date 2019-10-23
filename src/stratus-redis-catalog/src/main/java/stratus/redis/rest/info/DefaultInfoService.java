/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.info;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * All business logic to construct the model used by the info view
 *
 * @author joshfix
 * Created on 6/13/18
 */
@Service
@AllArgsConstructor
public class DefaultInfoService implements InfoService {

    private final RedisTemplate redisTemplate;

    @Override
    public Properties getInfo() {
        return redisTemplate.getConnectionFactory().getConnection().info();
    }
}
