/* (c) Planet Labs Inc. - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package stratus.gwc.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import stratus.redis.config.GeoServerWithEmbeddedRedisConfig;
import stratus.redis.repository.RedisRepository;

/**
 * Configuration for a Stratus instance with GWC, GS, and an embedded
 * {@link RedisRepository}
 *
 * Created by tbarsballe on 2018-03-14.
 */
@Slf4j
@Configuration
@EnableRedisRepositories({"stratus.redis.catalog.repository",
        "stratus.redis.geoserver.repository", "stratus.gwc.redis.repository"})
public class GWCWithEmbeddedRedisConfig extends GeoServerWithEmbeddedRedisConfig {

    @Bean
    public RedisGWCTestSupport redisGWCTestSupport() {
        return new RedisGWCTestSupport();
    }

}
