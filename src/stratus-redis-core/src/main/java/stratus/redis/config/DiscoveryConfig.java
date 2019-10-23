/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tingold
 */ 
@Slf4j
@AllArgsConstructor
@Configuration
@EnableDiscoveryClient
@Profile({"jedis-discovery","lettuce-discovery","aws"})
public class DiscoveryConfig {

    private final DiscoveryClient discoveryClient;
    private final RedisConfigProps configProps;

    @Bean
    @Profile("jedis-discovery")
    public JedisConnectionFactory jedisConnectionFactory() {
        log.info("Creating Jedis connection using discovery.");
        List<ServiceInstance> services = discoveryClient.getInstances("redis-catalog");
        List<String> nodes = getNodes(services);

        JedisConnectionFactory connectionFactory;
        // if there is more than one redis-catalog in discovery, assume it is a cluster
        if (nodes.size() > 1) {
            log.info("Found " + nodes.size() + " redis-catalog instances. Setting up cluster configuration.");
            connectionFactory = new JedisConnectionFactory(new RedisClusterConfiguration(nodes));
        } else {
            connectionFactory = new JedisConnectionFactory();
            if (!services.isEmpty()) {
                log.info("Found 1 redis-catalog instance.");
                connectionFactory.setHostName(services.get(0).getHost());
                connectionFactory.setPort(services.get(0).getPort());
            }
        }

        return connectionFactory;
    }

    @Bean
    @Profile("lettuce-discovery")
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("Creating Lettuce connection using discovery.");
        List<ServiceInstance> services = discoveryClient.getInstances("redis-catalog");
        List<String> nodes = getNodes(services);

        LettuceConnectionFactory connectionFactory;
        // if there is more than one redis-catalog in discovery, assume it is a cluster
        if (nodes.size() > 1) {
            log.info("Found " + nodes.size() + " redis-catalog instances. Setting up cluster configuration.");
            connectionFactory = new LettuceConnectionFactory(new RedisClusterConfiguration(nodes));
        } else {
            connectionFactory = new LettuceConnectionFactory();
            if (!services.isEmpty()) {
                log.info("Found 1 redis-catalog instance.");
                connectionFactory.setHostName(services.get(0).getHost());
                connectionFactory.setPort(services.get(0).getPort());
            }
        }
        connectionFactory.setShareNativeConnection(configProps.isShareNativeLettuceConnection());
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    public List<String> getNodes(List<ServiceInstance> services) {
        log.info("Listing redis-catalog instances...");
        List<String> nodes = new ArrayList<>(services.size());
        services.forEach(service -> {
            log.info("Host: " + service.getHost() + " port: " + service.getPort());
            nodes.add(service.getHost() + ":" + service.getPort());
        });
        return nodes;
    }

}
