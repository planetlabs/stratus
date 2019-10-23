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
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Slf4j
@Profile({"lettuce-cluster", "jedis-cluster"})
@Configuration
@AllArgsConstructor
public class ClusterConfig {

    private final RedisConfigProps configProps;

    @Bean
    @Profile("lettuce-cluster")
    public RedisConnectionFactory lettuceConnectionFactory(RedisConfigProps configProps) {
        log.info("Creating Lettuce Cluster connection");
        configProps.getCluster().getHosts().forEach(host -> log.info("Configuring cluster host {}", host));
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisClusterConfiguration());
        connectionFactory.setShareNativeConnection(configProps.isShareNativeLettuceConnection());
        if (null != configProps.getCluster().getPassword() && !configProps.getCluster().getPassword().isEmpty()) {
            connectionFactory.setPassword(configProps.getCluster().getPassword());
        }
        connectionFactory.afterPropertiesSet();
        return new LettuceConnectionFactory(redisClusterConfiguration());
    }

    @Bean
    @Profile("jedis-cluster")
    public RedisConnectionFactory jedisConnectionFactory(RedisConfigProps configProps) {
        log.info("Creating Jedis Cluster connection");
        configProps.getCluster().getHosts().forEach(host -> log.info("Configuring cluster host {}", host));
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisClusterConfiguration());
        if (null != configProps.getCluster().getPassword() && !configProps.getCluster().getPassword().isEmpty()) {
            connectionFactory.setPassword(configProps.getCluster().getPassword());
        }
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        return new RedisClusterConfiguration(configProps.getCluster().getHosts());
    }
}
