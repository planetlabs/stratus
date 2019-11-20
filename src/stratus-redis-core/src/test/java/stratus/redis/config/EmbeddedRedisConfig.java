/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.embedded.RedisServer;
import stratus.redis.repository.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Stack;

/**
 * Configuration for an embedded {@link RedisRepository}
 * Does not include any GeoServer configuration.
 *
 * Created by joshfix on 8/26/16.
 */
@Slf4j
@Configuration
public class EmbeddedRedisConfig {

    private static int REDIS_PORT = 16379;
    private Stack<RedisServer> redisServerStack = new Stack<>();

    @PostConstruct
    public void init() {
        int attempt = 0;
        boolean success = false;
        while (!success && attempt < 10) {
            try {
                int port = REDIS_PORT + attempt;
                log.info("Attempting " + attempt + " to create embedded redis server on port " + port);
                //Avoid https://github.com/kstyrc/embedded-redis/issues/45
                RedisServer redisServer = RedisServer.builder().setting("bind 127.0.0.1").port(REDIS_PORT + attempt).build();
                redisServer.start();
                redisServerStack.add(redisServer);
                success = true;
                log.info("Started embedded redis server on port " + port);
            } catch (Exception e) {
                log.info("Attempt failed. Trying again.");
                attempt++;
            }
        }
    }

    @PreDestroy
    public void destroy() {
        while (!redisServerStack.isEmpty()) {
            RedisServer redisServer = redisServerStack.pop();
            log.info("Stopping embedded redis server on port " + redisServer.ports().get(0));
            redisServer.stop();
        }
    }

    @Bean(name = "jedis-catalog")
    public RedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.getPoolConfig().setMaxIdle(1000);
        connectionFactory.getPoolConfig().setMaxTotal(2000);
        connectionFactory.getPoolConfig().setMaxWaitMillis(5000);
        connectionFactory.getPoolConfig().setTestOnBorrow(true);
        connectionFactory.setHostName("127.0.0.1");
        connectionFactory.setPort(REDIS_PORT);
        return connectionFactory;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new StratusRedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(false);
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> transactionalRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new StratusRedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }


    @Bean
    public RedisValueRepository redisValueRepository() { return new RedisValueRepositoryImpl(redisTemplate()); }

    @Bean
    public RedisHashRepository redisHashRepository() { return new RedisHashRepositoryImpl(redisTemplate()); }

    @Bean
    public RedisSetRepository redisSetRepository() { return new RedisSetRepositoryImpl(redisTemplate()); }


}
