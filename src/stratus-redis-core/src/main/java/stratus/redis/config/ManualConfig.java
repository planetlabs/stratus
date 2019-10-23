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
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

@Slf4j
@Profile({"lettuce-manual","jedis-manual"})
@Configuration
@AllArgsConstructor
public class ManualConfig {

    private final RedisConfigProps configProps;

    @Bean("lettuceConnectionFactory")
    @Profile("lettuce-manual")
    public RedisConnectionFactory lettuceConnectionFactory() {
        log.info("Creating manual Lettuce connection factory using host " + configProps.getManual().getHost() +
                ":" + configProps.getManual().getPort() + " using database " + configProps.getManual().getDatabase());

        // client configuration
        LettuceClientConfiguration clientConfiguration;
        if (configProps.isEnableConnectionPooling()) {
            clientConfiguration = LettucePoolingClientConfiguration.builder()
                    .poolConfig(PoolConfigFactory.lettucePoolConfig(configProps)).build();
        } else {
            clientConfiguration = LettuceClientConfiguration.builder()
                    .build();
        }

        // connection factory
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(buildStandaloneConfig(), clientConfiguration);
        connectionFactory.setShareNativeConnection(configProps.isShareNativeLettuceConnection());
        connectionFactory.setDatabase(configProps.getManual().getDatabase());

        return connectionFactory;
    }

    @Bean
    @Profile("jedis-manual")
    public RedisConnectionFactory jedisConnectionFactory() {
        log.info("Creating manual Jedis connection factory using host " + configProps.getManual().getHost() +
                ":" + configProps.getManual().getPort() + " using database " + configProps.getManual().getDatabase());

        JedisClientConfiguration clientConfiguration;
        if (configProps.isEnableConnectionPooling()) {
            clientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(PoolConfigFactory.jedisPoolConfig(configProps)).build();
        } else {
            clientConfiguration = JedisClientConfiguration.builder().build();
        }

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(buildStandaloneConfig(), clientConfiguration);

        return connectionFactory;
    }

    private RedisStandaloneConfiguration buildStandaloneConfig() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
                configProps.getManual().getHost(), configProps.getManual().getPort());
        if (null != configProps.getManual().getPassword() && !configProps.getManual().getPassword().isEmpty()) {
            redisConfig.setPassword(configProps.getManual().getPassword());
        }
        redisConfig.setDatabase(configProps.getManual().getDatabase());
        return redisConfig;
    }

}
