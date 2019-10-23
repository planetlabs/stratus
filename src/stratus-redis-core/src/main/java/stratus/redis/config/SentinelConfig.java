/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.util.HashSet;

@Slf4j
@Profile({"jedis-sentinel", "lettuce-sentinel"})
@Configuration
@AllArgsConstructor
public class SentinelConfig {

    private final RedisConfigProps configProps;

    @Profile("lettuce-sentinel")
    @Bean("lettuceConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory() {
        log.info("Creating Lettuce Sentinel connection using Sentinel master \"{}\".", configProps.getSentinel().getMaster());
        configProps.getSentinel().getHosts().forEach(host -> log.info("Configuring sentinel host {}", host));
        LettuceConnectionFactory connectionFactory;

        // configure pooling
        if (configProps.isEnableConnectionPooling()) {
            connectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration(),
                    LettucePoolingClientConfiguration.builder()
                            .poolConfig(PoolConfigFactory.lettucePoolConfig(configProps)).build());
        } else {
            connectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration());
        }
        connectionFactory.setShareNativeConnection(configProps.isShareNativeLettuceConnection());

        return connectionFactory;
    }

    @Profile("jedis-sentinel")
    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        log.info("Creating Jedis Sentinel connection using Sentinel master \"{}\".", configProps.getSentinel().getMaster());
        configProps.getSentinel().getHosts().forEach(host -> log.info("Configuring sentinel host {}", host));

        JedisClientConfiguration clientConfiguration;
        if (configProps.isEnableConnectionPooling()) {
            clientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(PoolConfigFactory.jedisPoolConfig(configProps)).build();
        } else {
            clientConfiguration = JedisClientConfiguration.builder().build();
        }
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisSentinelConfiguration(), clientConfiguration);

        return connectionFactory;
    }

    @Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration(
                configProps.getSentinel().getMaster(),
                new HashSet<>(configProps.getSentinel().getHosts()));

        if (null != configProps.getSentinel().getPassword() && !configProps.getSentinel().getPassword().isEmpty()) {
            redisSentinelConfiguration.setPassword(configProps.getSentinel().getPassword());
        }
        return redisSentinelConfiguration;
    }
}
