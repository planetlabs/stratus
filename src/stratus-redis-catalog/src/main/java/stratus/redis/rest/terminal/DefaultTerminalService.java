/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.rest.terminal;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

/**
 * All business logic to construct models used by the terminal view
 *
 * @author joshfix
 * Created on 6/15/18
 */
@Service
@AllArgsConstructor
public class DefaultTerminalService implements TerminalService {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public TerminalModel getTerminalModel() {
        TerminalModel model = new TerminalModel();
        if (redisConnectionFactory instanceof LettuceConnectionFactory) {
            model.setRedisHost(((LettuceConnectionFactory)redisConnectionFactory).getHostName());
            model.setRedisPort(((LettuceConnectionFactory)redisConnectionFactory).getPort());
        } else if (redisConnectionFactory instanceof JedisConnectionFactory) {
            model.setRedisHost(((JedisConnectionFactory)redisConnectionFactory).getHostName());
            model.setRedisPort(((JedisConnectionFactory)redisConnectionFactory).getPort());
        }
        return model;
    }
}
