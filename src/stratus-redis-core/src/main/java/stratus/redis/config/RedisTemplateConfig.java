/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.redis.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author joshfix
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties
public class RedisTemplateConfig {

    private final RedisConnectionFactory redisConnFactory;
    private final RedisConfigProps configProps;

    @Bean
    public RedisTemplate<String, Object> transactionalRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = buildRedisTemplate();

        boolean enableTransactionSupport = false;
        // if we are not using parallel queries, and either ows or rest caching is enabled, we need transaction support
        if ((configProps.getCaching().isEnableOwsCaching() || configProps.getCaching().isEnableRestCaching())
                && !configProps.getCaching().isUseParallelQueries()) {
            enableTransactionSupport = true;
        }
        redisTemplate.setEnableTransactionSupport(enableTransactionSupport);
        return redisTemplate;
    }

    /**
     * Transactional mode binds a connection to the current thread. Cleanup happens only any only if the transaction is
     * completed/aborted.  It is therefore necessary to use a separate redis template for the repositories than for
     * caching, which relies on transaction support.
     *
     * @return
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = buildRedisTemplate();
        redisTemplate.setEnableTransactionSupport(false);
        return redisTemplate;
    }

    private RedisTemplate<String, Object> buildRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new StratusRedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnFactory);
        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
        redisTemplate.setValueSerializer(redisTemplate.getStringSerializer());
        redisTemplate.setHashKeySerializer(redisTemplate.getStringSerializer());
        return redisTemplate;
    }

}
