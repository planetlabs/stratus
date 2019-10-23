/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple connection factory discovery via AWS tags
 */
@Slf4j
@Getter
@Setter
@Configuration
@Profile({"jedis-aws-tag-discovery","lettuce-aws-tag-discovery"})
public class AwsConfig {

    @Value("${stratus.catalogTag:redis-catalog}")
    private String catalogTag;

    @Value("${stratus.catalogPortTag:redis-port}")
    private String catalogPortTag;

    private String defaultCatalogPort = "6379";

    @Autowired
    private RedisConfigProps configProps;

    @Bean
    @Profile("jedis-aws-tag-discovery")
    public RedisConnectionFactory jedisConnectionFactory() {
        List<String> nodes = getNodes();


        // if there is more than one redis-catalog in discovery, assume it is a cluster
        if (nodes.size() > 1) {
            log.info("Found " + nodes.size() + " redis-catalog instances. Setting up cluster configuration.");
            return new JedisConnectionFactory(new RedisClusterConfiguration(nodes));
        } else {
            if (!nodes.isEmpty()) {
                log.info("Found 1 redis-catalog instance.");
                String redisCatalog = nodes.get(0);
                String[] parts = redisCatalog.split(":");
                RedisStandaloneConfiguration standaloneConfig =
                        new RedisStandaloneConfiguration(parts[0], Integer.valueOf(parts[1]));
                return new JedisConnectionFactory(standaloneConfig);
            }
        }
        return null;
    }

    @Bean
    @Profile("lettuce-aws-tag-discovery")
    public RedisConnectionFactory lettuceConnectionFactory() {
        List<String> nodes = getNodes();

        LettuceConnectionFactory connectionFactory;
        // if there is more than one redis-catalog in discovery, assume it is a cluster
        if (nodes.size() > 1) {
            log.info("Found " + nodes.size() + " redis-catalog instances. Setting up cluster configuration.");
            connectionFactory = new LettuceConnectionFactory(new RedisClusterConfiguration(nodes));
        } else {
            if (!nodes.isEmpty()) {
                log.info("Found 1 redis-catalog instance.");
                connectionFactory = new LettuceConnectionFactory(getStandaloneConfig(nodes));
            } else {
                return null;
            }
        }
        connectionFactory.setShareNativeConnection(configProps.isShareNativeLettuceConnection());
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    private RedisStandaloneConfiguration getStandaloneConfig(List<String> nodes) {
        String redisCatalog = nodes.get(0);
        String[] parts = redisCatalog.split(":");
        return new RedisStandaloneConfiguration(parts[0], Integer.valueOf(parts[1]));
    }

    public List<String> getNodes() {

        AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

        DescribeInstancesRequest diRequest = new DescribeInstancesRequest();
        Filter tagFilter = new Filter("tag-key", Collections.singletonList(catalogTag));
        diRequest.setFilters(Collections.singleton(tagFilter));

        List<String> nodes = new ArrayList<>();
        DescribeInstancesResult describeInstancesResult = ec2.describeInstances(diRequest);
        describeInstancesResult.getReservations().forEach(r -> r.getInstances().forEach(instance -> {
            String host = instance.getPublicDnsName();

            String port = instance.getTags().stream()
                    .filter(t -> t.getKey().equals(catalogPortTag))
                    .findFirst()
                    .map(Tag::getValue)
                    .orElse(defaultCatalogPort);

            nodes.add(host + ":" + port);
        }));
        return nodes;
    }

}
