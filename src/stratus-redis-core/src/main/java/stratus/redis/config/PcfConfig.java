
/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.common.RedisServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

/**
 *
 * @author tingold
 */
@Slf4j
@AllArgsConstructor
@Configuration
@Profile("cloud")
@ConditionalOnBean(RedisConfigProps.class)
public class PcfConfig {

    private final RedisConfigProps configProps;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        CloudFactory cloudFactory = new CloudFactory();
        Cloud cloud = cloudFactory.getCloud();
        RedisServiceInfo serviceInfo = (RedisServiceInfo) cloud.getServiceInfo(configProps.getPcf().getServiceId());
        String serviceID = serviceInfo.getId();
        // Get Jedis connection factory, and use it to get credentials
        JedisConnectionFactory connectionFactory = (JedisConnectionFactory)cloud.getServiceConnector(serviceID, RedisConnectionFactory.class, null);

        // redis configuration (use Jedis config)
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
                connectionFactory.getHostName(), connectionFactory.getPort());
        redisConfig.setPassword(connectionFactory.getPassword());


        LettucePoolingClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(PoolConfigFactory.lettucePoolConfig(configProps)).useSsl().build();

        LettuceConnectionFactory lettuce = new LettuceConnectionFactory(redisConfig, clientConfiguration);

        //connectionFactory.setPoolConfig(PoolConfigFactory.jedisPoolConfig(configProps));
        //connectionFactory.setUsePool(true);

        //return connectionFactory;
        return lettuce;
    }
    @Bean
    public RestTemplateBuilder restTemplateConfig(){
        return new RestTemplateBuilder();
    }

}
